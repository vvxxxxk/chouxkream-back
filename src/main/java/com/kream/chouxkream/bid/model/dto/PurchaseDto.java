package com.kream.chouxkream.bid.model.dto;

import com.kream.chouxkream.bid.model.entity.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseDto {

    public PurchaseDto(Long userNo, Long addressNo, Long paymentNo, int price) {
        this.userNo = userNo;
        this.addressNo = addressNo;
        this.paymentNo = paymentNo;
        this.price = price;
    }

    private Long userNo;
    private Long addressNo;
    private Long paymentNo;
    private int price;
}
