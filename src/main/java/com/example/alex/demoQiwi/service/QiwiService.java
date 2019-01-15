package com.example.alex.demoQiwi.service;

import com.example.alex.demoQiwi.configuration.QiwiConfiguration;
import com.example.alex.demoQiwi.enums.PaymentStatus;
import com.example.alex.demoQiwi.exception.QiwiServiceException;
import com.example.alex.demoQiwi.model.QiwiNotification;
import com.example.alex.demoQiwi.request.BasePaymentRequest;
import com.example.alex.demoQiwi.response.PaymentResponse;
import com.example.alex.demoQiwi.response.QiwiBillResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.Bill;
import com.qiwi.billpayments.sdk.model.BillStatus;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.Notification;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import com.qiwi.billpayments.sdk.utils.BillPaymentsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Map;

import static org.apache.http.HttpHeaders.AUTHORIZATION;


@Service
@Slf4j
@ConditionalOnProperty("qiwi.enabled")
public class QiwiService {

    private static final String TEMPLATE_NAME = "qiwi";
    private static final String CHECK_TRANSACTION_STATUS_URL = "https://api.qiwi.com/partner/bill/v1/bills/";
    private static final String SIGNATURE_HEADER = "X-Api-Signature-SHA256";
    private static final String BEARER_HEADER = "Bearer ";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QiwiConfiguration qiwiConfiguration;

    public BasePaymentRequest getPaymentRequest(String currency, String paymentId, BigDecimal amount, String userId) throws URISyntaxException {

        try {
            CreateBillInfo billInfo = initBill(currency, paymentId, amount, userId);

            BillPaymentClient client = BillPaymentClientFactory.createDefault(qiwiConfiguration.getSecretKey());
            BillResponse response = client.createBill(billInfo);
            log.info("Received response of create bill, response={}", response);
            return createBasePaymentRequest(response);
        } catch (Exception e) {
            log.warn("Get payment request error", e);
            throw new IllegalStateException(e);
        }
    }


    private CreateBillInfo initBill(String currency, String paymentId, BigDecimal amount, String userId) {
        MoneyAmount moneyAmount = new MoneyAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP),
                Currency.getInstance(currency));
        Customer customer = new Customer("", userId, "");
        return new CreateBillInfo(paymentId, moneyAmount, qiwiConfiguration.getComment(),
                ZonedDateTime.now().plusDays(45), customer, qiwiConfiguration.getSuccessUrl()
        );
    }

    private BasePaymentRequest createBasePaymentRequest(BillResponse response) {
        QiwiBillResponse qiwiBillResponse = new QiwiBillResponse(StringUtils.substringAfter(response.getPayUrl(), "invoice_uid="));
        BasePaymentRequest<QiwiBillResponse> basePaymentRequest = new BasePaymentRequest<>();
        basePaymentRequest.setPaymentRequest(qiwiBillResponse);
        basePaymentRequest.setPaymentSystemUrl(qiwiConfiguration.getPaymentUrl());
        basePaymentRequest.setTemplate(TEMPLATE_NAME);
        return basePaymentRequest;
    }


    public PaymentResponse handlePaymentResponse(HttpServletRequest request) {

        try {
            QiwiNotification qiwiNotification = objectMapper.readValue(request.getReader(), QiwiNotification.class);
            checkSHA(qiwiNotification, request.getHeader(SIGNATURE_HEADER));

            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                HttpGet httpGet = new HttpGet(CHECK_TRANSACTION_STATUS_URL + qiwiNotification.getBill().getBillId());
                httpGet.setHeader(AUTHORIZATION, BEARER_HEADER + qiwiConfiguration.getSecretKey());
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

                String entity = EntityUtils.toString(httpResponse.getEntity());
                if (httpResponse.getStatusLine().getStatusCode() != 200) {

                    log.warn("Handle exception for operation check status of transaction, message={}, transactionId={}",
                            entity, qiwiNotification.getBill().getBillId());
                    throw new QiwiServiceException(objectMapper.reader().forType(Map.class).readValue(entity));
                }

                BillResponse billResponse = objectMapper.reader().forType(BillResponse.class).readValue(entity);

                compareNotificationWithBillResponse(qiwiNotification, billResponse);
            }

            chekInternalPayment(qiwiNotification.getBill().getBillId());

            return getPaymentResponse(qiwiNotification);

        } catch (Exception e) {
            log.warn("Handle payment response error", e);
//            throw new PaymentTransportException(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void checkSHA(QiwiNotification qiwiNotification, String signature) {
        Notification notification = new Notification(
                new Bill(
                        qiwiNotification.getBill().getSiteId(),
                        qiwiNotification.getBill().getBillId(),
                        new MoneyAmount(
                                qiwiNotification.getBill().getAmount().getValue(),
                                qiwiNotification.getBill().getAmount().getCurrency()
                        ),
                        qiwiNotification.getBill().getStatus().getValue()
                ),
                qiwiNotification.getVersion()
        );

        boolean checkResult = BillPaymentsUtils.checkNotificationSignature(signature, notification, qiwiConfiguration.getSecretKey());
        if (!checkResult) {
            log.warn("Verification of digital signature authentication failed,signature={}, notification={}", signature, qiwiNotification);
            throw new IllegalArgumentException(String.format("Verification of digital signature authentication failed,signature=%s, notification=%s", signature, qiwiNotification));
        }
    }

    private void compareNotificationWithBillResponse(QiwiNotification notification, BillResponse response) {
        if (!notification.getBill().getBillId().equals(response.getBillId()) ||
                !notification.getBill().getSiteId().equals(response.getSiteId()) ||
                !notification.getBill().getAmount().getValue().equals(response.getAmount().getValue()) ||
                !notification.getBill().getAmount().getCurrency().equals(response.getAmount().getCurrency())) {
            log.warn("Notification data don't match with bill response result, notification={}, response={}", notification, response);
            throw new IllegalArgumentException(String.format("Notification data don't match with bill response result, notification=%s, response=%s", notification, response));
        }
    }

    private void chekInternalPayment(String paymentId) {
//        Payment payment = paymentDataService.findOne(paymentId);
//        if (payment.getStatus() != PaymentStatus.PENDING) {
//        log.warn("Can't update payment with paymentId={} because it status={}", paymentId, payment.getStatus().name())
//            throw new DataProcessingException(String.format("Can't update payment with paymentId=%s because it status=%s", paymentId, payment.getStatus().name()));
//        }
    }

    private PaymentResponse getPaymentResponse(QiwiNotification notification) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentSystem(getPaymentSystemCode());
        if (notification.getBill().getAmount() != null) {
            paymentResponse.setAmount(notification.getBill().getAmount().getValue());
            paymentResponse.setCurrency(notification.getBill().getAmount().getCurrency().getCurrencyCode());
        }

        if (notification.getBill().getCustomer() != null) {
            paymentResponse.setUserId(notification.getBill().getCustomer().getAccount());
        }

        paymentResponse.setTransactionId(notification.getBill().getBillId());

        if (notification.getBill().getStatus().getValue() == BillStatus.PAID) {
            paymentResponse.setStatus(PaymentStatus.PROCESSED);

        } else if (notification.getBill().getStatus().getValue() == BillStatus.WAITING) {
            paymentResponse.setStatus(PaymentStatus.PENDING);

        } else {
            paymentResponse.setStatus(PaymentStatus.FAILED);
        }

        return paymentResponse;
    }

    private String getPaymentSystemCode() {
        return "QiwiMoney";
    }
}
