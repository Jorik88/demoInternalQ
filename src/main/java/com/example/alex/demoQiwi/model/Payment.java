package com.example.alex.demoQiwi.model;

import com.example.alex.demoQiwi.enums.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Payment {

    @Id
    private String paymentId;
    private String paymentSystem;
    private String currency;
    private BigDecimal amount;
    private String userId;
    private String transactionId;
    private String userAccount;
    private PaymentStatus status;
    private Long updateTimestamp;
    private String lastFourNumeralsOfCard;
    private String cardToken;
    private String senderCardMask;

    public Payment(String paymentId, String paymentSystem, String currency, BigDecimal amount, String userId) {
        this.paymentId = paymentId;
        this.paymentSystem = paymentSystem;
        this.currency = currency;
        this.amount = amount;
        this.userId = userId;
        this.status = PaymentStatus.PENDING;
        this.updateTimestamp = System.currentTimeMillis();
    }
}
