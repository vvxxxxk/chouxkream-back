package com.kream.chouxkream.product.repository;

import com.kream.chouxkream.product.model.entity.ProductSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    Optional<ProductSize> findById(Long productSizeNo);

    Page<ProductSize> findByProductSizeNoIn(List<Long> productSizeNoList, Pageable pageable);
}
