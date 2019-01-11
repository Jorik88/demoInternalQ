package com.example.alex.demoQiwi;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

public class TestCurrency {

    @Test
    public void testGetCurrency() {
        Currency usd = Currency.getInstance("RUB");
        System.out.println(usd.getNumericCode() + " " + usd.getCurrencyCode());
    }

    @Test
    public void testAmount() {
        BigDecimal bigDecimal = BigDecimal.valueOf(123.4343).setScale(2, BigDecimal.ROUND_HALF_UP);
        System.out.println(bigDecimal);
    }
}
