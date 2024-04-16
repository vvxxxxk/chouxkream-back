package com.kream.chouxkream.product.model.dto;

import com.kream.chouxkream.product.model.entity.Brand;
import com.kream.chouxkream.product.model.entity.Category;
import com.kream.chouxkream.product.model.entity.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class ProductDetailDto {

    public ProductDetailDto(Product product) {

        this.productNo = product.getProductNo();
        this.productTitle = product.getProductTitle();
        this.productSubTitle = product.getProductSubTitle();
        this.modelId = product.getModelId();
        this.productColor = product.getProductColor();
        this.releasePrice = product.getReleasePrice();
        this.releaseDate = product.getReleaseDate();

        Brand brand = product.getBrand();
        if (brand != null) {
            this.brandName = brand.getBrandName();
            this.brandSubName = brand.getBrandSubName();
            this.brandLogoUrl = brand.getBrandLogoUrl();
        }

        Category category = product.getCategory();
        if (category != null) {
            this.categoryNo = category.getCategoryNo();
            this.categoryName = category.getCategoryName();
        }
    }

    private Long productNo;
    private String productTitle;
    private String productSubTitle;
    private String modelId;
    private String productColor;
    private Integer releasePrice;
    private Timestamp releaseDate;

    private String brandName;
    private String brandSubName;
    private String brandLogoUrl;

    private Long categoryNo;
    private String categoryName;
}
