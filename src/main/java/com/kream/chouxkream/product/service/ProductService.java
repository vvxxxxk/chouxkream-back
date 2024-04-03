package com.kream.chouxkream.product.service;

import com.kream.chouxkream.product.ProductSpecification;
import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final String POPULAR_SEARCH_KEY = "popular_searches";
    private final RedisTemplate<String, Object> redisTemplate;


    public List<Product> search(SearchDTO searchDTO){
        Specification<Product> spec = (root, query, criteriaBuilder) -> null;
        if (searchDTO.getKeyword() != null) {
            spec = spec.and(ProductSpecification.searchKeyword(searchDTO.getKeyword()));
            redisTemplate.opsForZSet().incrementScore(POPULAR_SEARCH_KEY, searchDTO.getKeyword(), 1);
        }
        if(searchDTO.getBrand() != null)
            spec = spec.and(ProductSpecification.equalBrand(searchDTO.getBrand()));
        if(searchDTO.getCategory() != null)
            spec = spec.and(ProductSpecification.equalCategory(searchDTO.getCategory()));
        if(searchDTO.getColor() != null)
            spec = spec.and(ProductSpecification.equalColor(searchDTO.getColor()));
        List<Product> list = productRepository.findAll(spec);


        return list;
    }

    public List<String> getPopularSearches() {
        Set<ZSetOperations.TypedTuple<Object>> popularSearches = redisTemplate.opsForZSet().reverseRangeWithScores(POPULAR_SEARCH_KEY, 0, 10);
        return popularSearches.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
