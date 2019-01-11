package com.example.alex.demoQiwi;

import com.example.alex.demoQiwi.model.QiwiNotification;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.qiwi.billpayments.sdk.model.Notification;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.IOException;

public class TestCreateNotification {

    @Test
    public void testNotification() throws IOException {
        String value = "{ \"bill\":\n" +
                "  {  \n" +
                "     \"siteId\":\"23044\",\n" +
                "     \"billId\":\"1519892138404fhr7i272a2\",\n" +
                "     \"amount\":{  \n" +
                "        \"value\":\"100\",\n" +
                "        \"currency\":\"RUB\"\n" +
                "     },\n" +
                "     \"status\":{  \n" +
                "        \"value\":\"PAID\",\n" +
                "        \"datetime\":\"2018-03-01T11:16:12\"\n" +
                "     },\n" +
                "     \"customer\":{},\n" +
                "     \"customFields\":{},\n" +
                "     \"creationDateTime\":\"2018-03-01T11:15:39\",\n" +
                "     \"expirationDateTime\":\"2018-04-01T11:15:39\"\n" +
                "   },\n" +
                "  \"version\":\"1\"\n" +
                "}";
        ObjectMapper objectMapper = new ObjectMapper();
        Notification notification1 = objectMapper.readValue(value, new TypeReference<Object>() {});

        System.out.println(notification1);

//        SerializationUtils.clone(notification1);
    }

    @Test
    public void testCreateClient() throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:8080/qiwi");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        System.out.println(httpResponse);
    }

    @Test
    public void test() throws IOException {
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
        QiwiNotification qiwiBill = objectMapper.readValue(json, QiwiNotification.class);
        System.out.println(qiwiBill);
    }

    @Test
    public void testGsonLib() {
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

//        QiwiNotification qiwiNotification = new Gson().fromJson(json, QiwiNotification.class);
        Notification qiwiNotification = new Gson().fromJson(json, Notification.class);
        System.out.println(qiwiNotification);
    }
}
