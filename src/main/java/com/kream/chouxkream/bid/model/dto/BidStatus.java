package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;

@Getter
public enum BidStatus {

    BID_PROGRESS("bid_progress"),
    BID_CANCEL("bid_cancel"),
    BID_COMPLETE("bid_complete");

    private final String status;

    BidStatus(String status) {
        this.status = status;
    }
}
