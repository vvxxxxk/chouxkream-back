package com.kream.chouxkream.product.service;

import com.kream.chouxkream.category.model.entity.Category;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductRepositroy;
import com.kream.chouxkream.productimages.model.entity.ProductImages;
import com.kream.chouxkream.productsize.model.entity.ProductSize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepositroy productRepositroy;

    public void TestMethod(long product_no) {

        Optional<Product> optionalProduct = productRepositroy.findById(product_no);
        // 상품이 있다면, 상품과 관련된 브랜드, 카테고리, 상품 사이즈, 상품 이미지 정보는 자동으로 가져올 수 있음
        // [질문]
        // "그렇다면, 입찰 관련 정보는? bidRepository를 productService에서 참조하는게 맞는가?"
    }

}
    public void ProductTestMethod(long product_no) {

        Optional<Product> optionalProduct = productRepositroy.findById(product_no);

        // 테스트 코드라 무시하셔도 됩니다.
        if (optionalProduct.isPresent()) {
            System.out.println("상품 O");
            Product product = optionalProduct.get();
            Long productNo = product.getProductNo();
            String productTitle = product.getProductTitle();
            String productSubTitle = product.getProductSubTitle();
            String modelId = product.getModelId();
            String productColor = product.getProductColor();
            Integer releasePrice = product.getReleasePrice();
            Timestamp releaseDate = product.getReleaseDate();
            Timestamp createDate = product.getCreateDate();
            Timestamp updateDate = product.getUpdateDate();
            int viewsCount = product.getViewsCount();
            String metaKeyword = product.getMetaKeyword();
            String metaDesc = product.getMetaDesc();
            boolean isActive = product.isActive();
            String brandId = product.getBrand().getBrandId();
            Category category = product.getCategory();
            Long categoryNo;
            if (category == null) {
                System.out.println("카테고리 NULL");
                categoryNo = 0L;
            } else {
                categoryNo = category.getCategoryNo();
            }
            Set<ProductImages> productImages = product.getProductImages();
            Set<ProductSize> productSizes = product.getProductSizes();

            System.out.println("productNo = " + productNo);
            System.out.println("productTitle = " + productTitle);
            System.out.println("productSubTitle = " + productSubTitle);
            System.out.println("modelId = " + modelId);
            System.out.println("productColor = " + productColor);
            System.out.println("releasePrice = " + releasePrice);
            System.out.println("releaseDate = " + releaseDate);
            System.out.println("createDate = " + createDate);
            System.out.println("updateDate = " + updateDate);
            System.out.println("viewsCount = " + viewsCount);
            System.out.println("metaKeyword = " + metaKeyword);
            System.out.println("metaDesc = " + metaDesc);
            System.out.println("isActive = " + isActive);
            System.out.println("brandId = " + brandId);
            System.out.println("categoryNo = " + categoryNo);
            System.out.println("---------------------------------------------");
            for (ProductImages productImage : productImages) {
                System.out.println("productImage.getImageUrl() = " + productImage.getImageUrl());
            }
            for (ProductSize productSize : productSizes) {
                System.out.println("productSize.getSizeName() = " + productSize.getSizeName());
            }
        } else {
            System.out.println("상품 X");
        }
    }
}
