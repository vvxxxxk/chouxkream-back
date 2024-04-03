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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @GetMapping("/api/search")
    public Map<String,Object> search(@RequestBody SearchDTO searchDTO){
        Map<String, Object> result = new HashMap<>();
        result.put("is_success", true);
        result.put("data", productService.search(searchDTO));
        return result;
    }

    @GetMapping("/api/search/rank")
    public List<String> searchRankList(){
        return productService.getPopularSearches();
    }
}
