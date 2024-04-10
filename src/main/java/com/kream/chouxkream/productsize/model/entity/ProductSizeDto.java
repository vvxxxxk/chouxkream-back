package com.kream.chouxkream.productsize.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductSizeDto {
    private Long productSizeNo;
    private String sizeName;
    private int stock;
    private int sellCount;

    private Long productNo;
    private Long wishListNo;

}
