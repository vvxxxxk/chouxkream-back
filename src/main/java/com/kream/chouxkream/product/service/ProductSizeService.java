package com.kream.chouxkream.product.service;

import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.repository.ProductSizeRepository;
import com.kream.chouxkream.user.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductSizeService {
    private final ProductSizeRepository productSizeRepository;

    public ProductSize getProductSizeByNo(Long productSizeNo) {
        return this.productSizeRepository.findById(productSizeNo)
                .orElseThrow(()->new ResourceNotFoundException("ProductSize not found."));
    }
}
