package com.kream.chouxkream.product.controller;

import com.kream.chouxkream.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/test")
    public void ProductTestMethod() {

        productService.ProductTestMethod(3);
    }
}
