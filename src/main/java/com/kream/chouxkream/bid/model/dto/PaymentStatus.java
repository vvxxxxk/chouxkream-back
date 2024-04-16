package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    payment_progress("payment_progress"),
    payment_cancel("payment_cancel"),
    payment_complete("payment_complete");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }
}
