package com.kream.chouxkream.product.model.dto;

import com.kream.chouxkream.product.model.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private Long productNo;
    private String productTitle;
    private String productSubTitle;
    private Integer releasePrice;
    private String brand;
    private List<String> productImageUrl;
}
