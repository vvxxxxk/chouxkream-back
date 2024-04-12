package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    mobile_payment("mobile_payment"),
    card_payment("card_payment"),
    deposit_payment("deposit_payment");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }
}
