package com.kream.chouxkream.product.controller;

import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.product.model.dto.ProductDetailDto;
import com.kream.chouxkream.product.model.dto.ProductSizeDetailDto;
import com.kream.chouxkream.product.model.entity.*;
import com.kream.chouxkream.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{product_no}")
    public ResponseEntity<ResponseMessageDto> getProductDetail(@PathVariable("product_no") Long productNo) {

        Optional<Product> optionalProduct = productService.getProductById(productNo);

        if (optionalProduct.isEmpty()) {

            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        Product product = optionalProduct.get();
        ProductDetailDto productDetailDto = new ProductDetailDto(product);

        List<ProductSize> productSizeList = productService.getProductSizeById(productNo);
        List<ProductSizeDetailDto> productSizeDetailDtoList = new ArrayList<>();
        for (ProductSize productSize : productSizeList) {

            ProductSizeDetailDto productSizeDetailDto = new ProductSizeDetailDto(productSize);
            productSizeDetailDtoList.add(productSizeDetailDto);
        }

        List<String> productImageUrlByProductNo = productService.getProductImageUrlByProductNo(productNo);

        Map<String, Integer> sellPriceList = new HashMap<>();
        Map<String, Integer> buyPriceList = new HashMap<>();
        for (ProductSizeDetailDto productSizeDetailDto : productSizeDetailDtoList) {

            Integer minSellPrice = productService.getMinSellPrice(productSizeDetailDto.getProductSizeNo());
            Integer maxBuyPrice = productService.getMaxBuyPrice(productSizeDetailDto.getProductSizeNo());

            sellPriceList.put(productSizeDetailDto.getSizeName(), minSellPrice);
            buyPriceList.put(productSizeDetailDto.getSizeName(), maxBuyPrice);
        }

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("product", productDetailDto);
        responseMessageDto.addData("size", productSizeDetailDtoList);
        responseMessageDto.addData("images", productImageUrlByProductNo);
        responseMessageDto.addData("sell_info", sellPriceList);
        responseMessageDto.addData("buy_info", buyPriceList);

        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

}
