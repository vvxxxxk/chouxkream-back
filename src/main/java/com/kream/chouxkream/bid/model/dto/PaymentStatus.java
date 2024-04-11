package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    PAYMENT_PROGRESS("payment_progress"),
    PAYMENT_CANCEL("payment_cancel"),
    PAYMENT_COMPLETE("payment_complete");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }
}
