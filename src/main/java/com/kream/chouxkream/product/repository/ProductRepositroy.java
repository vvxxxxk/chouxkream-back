package com.kream.chouxkream.product.repository;

import com.kream.chouxkream.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepositroy extends JpaRepository<Product, Long> {

}
