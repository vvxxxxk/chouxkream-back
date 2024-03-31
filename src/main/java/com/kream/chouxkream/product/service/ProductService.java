package com.kream.chouxkream.product.service;

import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    /*
    public List<Product> search(SearchDTO searchDTO){

    }*/
}
