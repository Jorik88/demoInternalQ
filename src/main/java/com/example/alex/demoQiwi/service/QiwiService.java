package com.example.alex.demoQiwi.service;

import com.example.alex.demoQiwi.configuration.QiwiConfiguration;
import com.example.alex.demoQiwi.model.QiwiNotification;
import com.example.alex.demoQiwi.request.BasePaymentRequest;
import com.example.alex.demoQiwi.response.PaymentResponse;
import com.example.alex.demoQiwi.response.QiwiBillResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiwi.billpayments.sdk.client.BillPaymentClient;
import com.qiwi.billpayments.sdk.client.BillPaymentClientFactory;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.CreateBillInfo;
import com.qiwi.billpayments.sdk.model.in.Customer;
import com.qiwi.billpayments.sdk.model.out.BillResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Currency;


@Service
@Slf4j
@ConditionalOnProperty("qiwi.enabled")
public class QiwiService {

    private static final String TEMPLATE_NAME = "qiwi";
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
        }catch (Exception e) {
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


    public PaymentResponse handlePaymentResponse(HttpServletRequest request) throws IOException {

        QiwiNotification qiwiNotification = objectMapper.readValue(request.getReader(), QiwiNotification.class);


        return null;
    }
}
