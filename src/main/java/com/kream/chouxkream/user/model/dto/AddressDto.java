package com.kream.chouxkream.user.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {

    private Long addressNo; //필수
    private String receiverName; //필수
    private String receiverPhone; //필수
    private String zipcode; //필수
    private String address; //필수
    private String detailAddress; //필수 X
    private boolean defaultAddress;
}
