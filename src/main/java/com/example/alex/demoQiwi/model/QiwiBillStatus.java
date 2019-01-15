package com.example.alex.demoQiwi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qiwi.billpayments.sdk.model.BillStatus;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QiwiBillStatus {

    private BillStatus value;

    @JsonCreator
    public QiwiBillStatus(
            @JsonProperty(required = true, value = "value") BillStatus value) {
        this.value = value;
    }
}
