package com.kream.chouxkream.bid.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellingDto {

    private Long userNo; //ㅇㅇ
    private Long addressNo; // ㅇㅇ
    private String paymentMethod;
    private int price; //d유저입력


    private String account;
    private String bankName;

    public SellingDto(Long userNo, Long addressNo, String paymentMethod, int price, String account, String bankName) {
        this.userNo = userNo;
        this.addressNo = addressNo;
        this.paymentMethod = paymentMethod;
        this.price = price;
        this.account = account;
        this.bankName = bankName;
    }
}