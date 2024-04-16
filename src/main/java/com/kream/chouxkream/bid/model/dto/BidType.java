package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;

@Getter
public enum BidType {

    sell("sell"),
    buy("buy");

    private final String type;

    BidType(String type) {
        this.type = type;
    }
}
