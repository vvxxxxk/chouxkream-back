package com.kream.chouxkream.product.controller;

import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/api/search/auto")
    public Set<Object> search(@RequestParam("keyword") String keyword) {
        return productService.getSearchSuggestions(keyword);
    }
}
