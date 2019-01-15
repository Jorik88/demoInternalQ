package com.example.alex.demoQiwi.exception;

import lombok.Data;

import java.util.Map;

@Data
public class QiwiServiceException extends RuntimeException {
    private final Map<String, Object> info;
}
