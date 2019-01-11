package com.example.alex.demoQiwi.request;

import lombok.Data;

@Data
public class BasePaymentRequest<T> {
    private String paymentSystemUrl;
    private String template;
    private T paymentRequest;
}
