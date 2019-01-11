package com.example.alex.demoQiwi.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qiwi")
public class QiwiConfiguration {

    private String paymentUrl;
    private String successUrl;
    private String failUrl;
    private String comment;
    private String publicKey;
    private String secretKey;
}
