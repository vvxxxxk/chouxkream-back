package com.kream.chouxkream.product.repository;

import com.kream.chouxkream.product.model.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductImagesRepository extends JpaRepository<ProductImages, Long> {

    @Query("SELECT pi.imageUrl FROM ProductImages pi WHERE pi.product.productNo = :productNo")
    List<String> findImageUrlListByProductNo(Long productNo);
}
