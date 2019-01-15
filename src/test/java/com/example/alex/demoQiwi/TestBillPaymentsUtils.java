package com.example.alex.demoQiwi;

import com.example.alex.demoQiwi.model.QiwiNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiwi.billpayments.sdk.model.Bill;
import com.qiwi.billpayments.sdk.model.BillStatus;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.Notification;
import com.qiwi.billpayments.sdk.utils.BillPaymentsUtils;
import org.junit.Test;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.TreeMap;

public class TestBillPaymentsUtils {

    @Test
    public void testUtils() throws IOException {
        String json = "{ \"bill\":\n" +
                "  {  \n" +
                "     \"siteId\":\"23044\",\n" +
                "     \"billId\":\"1519892138404fhr7i272a2\",\n" +
                "     \"amount\":{  \n" +
                "        \"value\":\"100\",\n" +
                "        \"currency\":\"RUB\"\n" +
                "     },\n" +
                "     \"status\":{  \n" +
                "        \"value\":\"PAID\",\n" +
                "        \"changedDateTime\":\"2018-03-01T11:16:12\"\n" +
                "     },\n" +
                "     \"customer\":{},\n" +
                "     \"customFields\":{},\n" +
                "     \"creationDateTime\":\"2018-03-01T11:15:39\",\n" +
                "     \"expirationDateTime\":\"2018-04-01T11:15:39\"\n" +
                "   },\n" +
                "  \"version\":\"1\"\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        QiwiNotification qiwiBill = objectMapper.reader().forType(QiwiNotification.class).readValue(json);
        Notification notification = convertToNotification(qiwiBill);
        String joinFields = joinFields(notification);
        System.out.println(joinFields);
    }

    @Test
    public void testSignature() {
        String merchantSecret = "test-merchant-secret-for-signature-check";
        Notification notification = new Notification(
                new Bill(
                        "test",
                        "test_bill",
                        new MoneyAmount(
                                BigDecimal.ONE,
                                Currency.getInstance("RUB")
                        ),
                        BillStatus.PAID
                ),
                "3"
        );
        String validSignature = "07e0ebb10916d97760c196034105d010607a6c6b7d72bfa1c3451448ac484a3b";
        boolean b = BillPaymentsUtils.checkNotificationSignature(validSignature, notification, merchantSecret);//true
        System.out.println(b);
    }

    private Notification convertToNotification(QiwiNotification qiwiNotification) {
        return new Notification(
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
    }

    private String joinFields(Notification notification) {
        Map<String, String> fields = new TreeMap<String, String>() {{
            put("amount.currency", notification.getBill().getAmount().getCurrency().toString());
            put("amount.value", notification.getBill().getAmount().formatValue());
            put("billId", notification.getBill().getBillId());
            put("siteId", notification.getBill().getSiteId());
            put("status", notification.getBill().getStatus().getValue());
        }};
        return String.join("|", fields.values());
    }

    @Test
    public void convertJsonToMap() throws IOException {
        String json = "{\n" +
                "\t\"serviceName\": \"invoicing\",\n" +
                "\t\"errorCode\": \"auth.unauthorized\",\n" +
                "\t\"description\": \"Неверные аутентификационные данные\",\n" +
                "\t\"userMessage\": \"\",\n" +
                "\t\"datetime\": \"2018-04-09T18:31:42+03:00\",\n" +
                "\t\"traceId\" : \"\"\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.reader().forType(Map.class).readValue(json);
        System.out.println(map);
    }
}
