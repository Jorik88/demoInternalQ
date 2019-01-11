package com.example.alex.demoQiwi.entity;

import com.qiwi.billpayments.sdk.model.Bill;
import com.qiwi.billpayments.sdk.model.Notification;

import java.io.Serializable;

public class NotificationEntity extends Notification implements Serializable {

    public NotificationEntity(Bill bill, String version) {
        super(bill, version);
    }

}
