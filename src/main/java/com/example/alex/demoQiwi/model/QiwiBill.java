package com.example.alex.demoQiwi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import com.qiwi.billpayments.sdk.model.in.Customer;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QiwiBill {

    private String siteId;
    private String billId;
    private MoneyAmount amount;
    private QiwiBillStatus status;
    private Customer customer;

    @JsonCreator
    public QiwiBill(
            @JsonProperty(required = true, value = "siteId") String siteId,
            @JsonProperty(required = true, value = "billId") String billId,
            @JsonProperty(required = true, value = "amount") MoneyAmount amount,
            @JsonProperty(required = true, value = "status") QiwiBillStatus status,
            @JsonProperty(value = "customer") Customer customer) {
        this.siteId = siteId;
        this.billId = billId;
        this.amount = amount;
        this.status = status;
        this.customer = customer;
    }
}
