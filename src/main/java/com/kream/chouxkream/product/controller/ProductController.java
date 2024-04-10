package com.kream.chouxkream.product.controller;

import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.service.ProductService;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @GetMapping("/api/search")
    public Map<String,Object> search(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "category", required = false) List<Integer> category,
                                     @RequestParam(value = "brand", required = false) List<String> brand, @RequestParam(value = "color", required = false) List<String> color,
                                     @RequestParam(value = "sort", required = false) String sort, @RequestBody(required = false) SearchDTO searchDTO){
        searchDTO.setKeyword(keyword);
        searchDTO.setCategory(category);
        searchDTO.setBrand(brand);
        searchDTO.setColor(color);
        searchDTO.setSort(sort);
        Map<String, Object> result = new HashMap<>();
        result.put("is_success", true);
        result.put("data", productService.search(searchDTO));
        return result;
    }

    @GetMapping("/api/search/popular-keyword")
    public Map<String,Object> searchPopularKeyword(){
        Map<String, Object> result = new HashMap<>();
        result.put("is_success", true);
        result.put("data", productService.getPopularSearches());
        return result;
    }

    @GetMapping("/api/search/auto")
    public Map<String,Object> searchAutoComplete(@RequestParam("keyword") String keyword) {
        Map<String, Object> result = new HashMap<>();
        result.put("is_success", true);
        result.put("data", productService.getSearchSuggestions(keyword, 10));
        return result;
    }

    @PostMapping("/api/search/subtitle")
    public void saveSubTitle(){
       productService.saveSearchKeyword();
    }

    @GetMapping("/api/search/recent-keyword")
    public void searchRecentKeyword(){

    }
}
