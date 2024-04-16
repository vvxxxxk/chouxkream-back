package com.kream.chouxkream.product.model.dto;

import com.kream.chouxkream.product.model.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
public class ProductSizeDetailDto {

    public ProductSizeDetailDto(ProductSize productSize) {
        this.productSizeNo = productSize.getProductSizeNo();
        this.sizeName = productSize.getSizeName();
        this.stock = productSize.getStock();
    }

    private Long productSizeNo;
    private String sizeName;
    private int stock;
}
