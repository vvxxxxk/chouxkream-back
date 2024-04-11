package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    MOBILE_PAYMENT("mobile_payment"),
    CARD_PAYMENT("card_payment"),
    DEPOSIT_PAYMENT("deposit_payment");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }
}
