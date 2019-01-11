package com.example.alex.demoQiwi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qiwi.billpayments.sdk.model.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QiwiBillStatus {

    private BillStatus value;
}
