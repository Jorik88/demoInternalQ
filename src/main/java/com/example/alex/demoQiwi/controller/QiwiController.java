package com.example.alex.demoQiwi.controller;

import com.example.alex.demoQiwi.request.BasePaymentRequest;
import com.example.alex.demoQiwi.response.QiwiBillResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class QiwiController {

    private static final String PAYMENT_URL = "https://oplata.qiwi.com/form";

    @GetMapping(value = "/qiwiGet")
    public String sendForm(Model model) {
        QiwiBillResponse qiwiBillResponse = new QiwiBillResponse("d875277b-6f0f-445d-8a83-f62c7c07be77");
        BasePaymentRequest<QiwiBillResponse> paymentRequest = new BasePaymentRequest<>();
        paymentRequest.setPaymentRequest(qiwiBillResponse);
        model.addAttribute("paymentSystemUrl", PAYMENT_URL);
        model.addAttribute("paymentRequest", paymentRequest.getPaymentRequest());
        return "qiwi";
    }

    @GetMapping(value = "/qiwi")
    public String get(Model model) {
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
        model.addAttribute("paymentSystemUrl", "http://localhost:8080/qiwiPost");
        model.addAttribute("data", json);
        return "qiwiGet";
    }

    @PostMapping(value = "qiwiPost")
    public String qiwiPost(HttpServletRequest request) {
        System.out.println(request);
        return "";
    }
}
