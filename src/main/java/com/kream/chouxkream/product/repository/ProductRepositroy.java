package com.kream.chouxkream.product.repository;

import com.kream.chouxkream.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepositroy extends JpaRepository<Product, Long> {
}
