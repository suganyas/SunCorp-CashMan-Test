package com.suncorp.cashman.denominations;

import com.suncorp.cashman.interfaces.Currency;


public enum AustralianCurrency implements Currency {

    HUNDREDDOLLAR("AUD", 100),
    FIFTYDOLLAR("AUD", 50),
    TWENTYDOLLAR("AUD", 20),
    TENDOLLAR("AUD", 10),
    FIVEDOLLAR("AUD", 5),
    ONEDOLLAR("AUD", 1),
    TWODOLLAR("AUD", 2),
    TWENTYCENTS("AUD", 0.20),
    FIFTYCENTS("AUD", 0.50),
    FIVECENTS("AUD", 0.05),
    TENCENTS("AUD", 0.10);


    private String currencyCode;
    private Double value;

    AustralianCurrency(String currencyCode, double value) {
        this.currencyCode = currencyCode;
        this.value = Double.valueOf(value);
    }

    public Double getValue() {
        return value;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

}
