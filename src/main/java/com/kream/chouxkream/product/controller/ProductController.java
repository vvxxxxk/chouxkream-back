package com.kream.chouxkream.product.controller;

import com.kream.chouxkream.product.ProductSpecification;
import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductRepository;
import com.kream.chouxkream.product.service.ProductService;
import com.kream.chouxkream.user.model.dto.UserJoinDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;


    @GetMapping("/api/search")
    public List<Product> search(@RequestBody SearchDTO searchDTO){
        Specification<Product> spec = (root, query, criteriaBuilder) -> null;
        if (searchDTO.getKeyword() != null)
            spec = spec.and(ProductSpecification.likeKeyword(searchDTO.getKeyword()));

        List<Product> list = productRepository.findAll(spec);
        return list;
    }
}
