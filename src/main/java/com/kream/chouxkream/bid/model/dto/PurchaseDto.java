package com.kream.chouxkream.bid.model.dto;

import com.kream.chouxkream.bid.model.entity.Payment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseDto {

    private Long userNo; //ㅇㅇ
    private Long addressNo; // ㅇㅇ
    private String paymentMethod;
    private int price; //d유저입력
    private int pointToUse; // 유저입력

    public PurchaseDto(Long userNo, Long addressNo, String paymentMethod, int price, int pointToUse) {
        this.userNo = userNo;
        this.addressNo = addressNo;
        this.paymentMethod = paymentMethod;
        this.price = price;
        this.pointToUse = pointToUse;
    }
}
