package com.kream.chouxkream.product.controller;

import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.product.model.dto.ProductDetailDto;
import com.kream.chouxkream.product.model.dto.ProductSizeDetailDto;
import com.kream.chouxkream.product.model.entity.*;
import com.kream.chouxkream.product.service.ProductService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kream.chouxkream.product.model.dto.SearchDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @ApiOperation(value = "상품 상세 조회")
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

    @ApiOperation(value = "상품 검색")
    @GetMapping("/search")
    public ResponseEntity<ResponseMessageDto> search(@RequestParam(value = "keyword", required = false) String keyword,
                                                     @RequestParam(value = "category", required = false) List<Integer> category,
                                                     @RequestParam(value = "brand", required = false) List<String> brand,
                                                     @RequestParam(value = "color", required = false) List<String> color,
                                                     @RequestParam(value = "sort", required = false) String sort,
                                                     @RequestBody(required = false) SearchDTO searchDTO) {

        searchDTO.setKeyword(keyword);
        searchDTO.setCategory(category);
        searchDTO.setBrand(brand);
        searchDTO.setColor(color);
        searchDTO.setSort(sort);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessage = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessage.addData("product", productService.search(searchDTO));
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @ApiOperation(value = "인기 검색어")
    @GetMapping("/search/popular-keyword")
    public ResponseEntity<ResponseMessageDto> searchPopularKeyword() {

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessage = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessage.addData("keyword", productService.getPopularSearches());
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @ApiOperation(value = "검색 자동완성")
    @GetMapping("/search/auto")
    public ResponseEntity<ResponseMessageDto> searchAutoComplete(@RequestParam("keyword") String keyword) {

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessage = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessage.addData("product", productService.getSearchSuggestions(keyword, 10));
        if (keyword.isEmpty()) {
            responseMessage.addData("product", null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @ApiOperation(value = "특정 키워드(현재 프로젝트에서는 서브 타이틀이 기준)를 레디스드에 저장하는 기능 ")
    @PostMapping("/search/subtitle")
    public void saveSubTitle() {

        productService.saveSearchKeyword();
    }

    @ApiOperation(value = "회원 최근 검색어 조회")
    @GetMapping("/search/recent-keyword")
    public ResponseEntity<ResponseMessageDto> searchRecentKeyword() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessage = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessage.addData("keyword", productService.getRecentSearches(email));
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @ApiOperation(value = "회원 최근 검색어 저장")
    @PostMapping("/search/recent-keyword")
    public ResponseEntity<ResponseMessageDto> saveRecentKeyword(@RequestBody SearchDTO searchDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        productService.saveRecentSearches(email, searchDTO.getKeyword());
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessage = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }
}
