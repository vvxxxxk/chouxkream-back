package com.kream.chouxkream.productsize;

import com.kream.chouxkream.user.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSizeService {
    private final ProductSizeRepository productSizeRepository;

    public ProductSize getProductSizeByNo(Long productSizeNo) {
        return this.productSizeRepository.findById(productSizeNo)
                .orElseThrow(()->new ResourceNotFoundException("ProductSize not found."));
    }

}
