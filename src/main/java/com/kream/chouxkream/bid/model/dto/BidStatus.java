package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;

@Getter
public enum BidStatus {

    bid_progress("bid_progress"),
    bid_cancel("bid_cancel"),
    bid_complete("bid_complete");

    private final String status;

    BidStatus(String status) {
        this.status = status;
    }
}
