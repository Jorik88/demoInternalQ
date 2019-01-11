package com.example.alex.demoQiwi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qiwi.billpayments.sdk.model.MoneyAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QiwiBill {

    private String siteId;
    private String billId;
    private MoneyAmount amount;
    private QiwiBillStatus status;
}
