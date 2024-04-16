package com.kream.chouxkream.product.model.dto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductSizeDto {

    private Long productSizeNo;
    private String sizeName;

    // ProductImage Info
    private String imageUrl;

    public ProductSizeDto() {
        // 기본 생성자 추가
    }
}
