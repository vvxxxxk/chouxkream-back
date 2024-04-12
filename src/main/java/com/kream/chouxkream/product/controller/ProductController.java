package com.kream.chouxkream.product.controller;

import com.kream.chouxkream.common.model.entity.ResponseMessage;
import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.service.ProductService;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @GetMapping("/api/search")
    public ResponseEntity<ResponseMessage> search(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "category", required = false) List<Integer> category,
                                     @RequestParam(value = "brand", required = false) List<String> brand, @RequestParam(value = "color", required = false) List<String> color,
                                     @RequestParam(value = "sort", required = false) String sort, @RequestBody(required = false) SearchDTO searchDTO){
        searchDTO.setKeyword(keyword);
        searchDTO.setCategory(category);
        searchDTO.setBrand(brand);
        searchDTO.setColor(color);
        searchDTO.setSort(sort);
        ResponseMessage responseMessage = new ResponseMessage(200, "keyword search complete", null);
        responseMessage.addData("product", productService.search(searchDTO));
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    @GetMapping("/api/search/popular-keyword")
    public ResponseEntity<ResponseMessage> searchPopularKeyword(){
        ResponseMessage responseMessage = new ResponseMessage(200, "keyword search complete", null);
        responseMessage.addData("keyword", productService.getPopularSearches());
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    @GetMapping("/api/search/auto")
    public ResponseEntity<ResponseMessage> searchAutoComplete(@RequestParam("keyword") String keyword) {
        ResponseMessage responseMessage = new ResponseMessage(200, "keyword search complete", null);
        responseMessage.addData("product", productService.getSearchSuggestions(keyword, 10));
        if(keyword.isEmpty()){
            responseMessage.addData("product", null);
        }
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    @PostMapping("/api/search/subtitle")
    public void saveSubTitle(){
       productService.saveSearchKeyword();
    }

    @GetMapping("/api/search/recent-keyword")
    public ResponseEntity<ResponseMessage> searchRecentKeyword(){
        ResponseMessage responseMessage = new ResponseMessage(200, "keyword search complete", null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        responseMessage.addData("keyword", productService.getRecentSearches(email));
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }

    @PostMapping("/api/search/recent-keyword")
    public ResponseEntity<ResponseMessage> saveRecentKeyword(@RequestBody SearchDTO searchDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        productService.saveRecentSearches(email,searchDTO.getKeyword());
        ResponseMessage responseMessage = new ResponseMessage(200, "keyword save complete", null);
        responseMessage.addData("date", null);
        return ResponseEntity.status(responseMessage.getStatusCode()).body(responseMessage);
    }
}
