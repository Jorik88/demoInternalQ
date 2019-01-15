package com.example.alex.demoQiwi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QiwiNotification {

    private QiwiBill bill;
    private String version;

    @JsonCreator
    public QiwiNotification(
            @JsonProperty(required = true, value = "bill") QiwiBill bill,
            @JsonProperty(required = true, value ="version") String version) {
        this.bill = bill;
        this.version = version;
    }
}
