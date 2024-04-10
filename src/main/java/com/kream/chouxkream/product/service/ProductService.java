package com.kream.chouxkream.product.service;

import com.kream.chouxkream.product.ProductSpecification;
import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductMapping;
import com.kream.chouxkream.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final String POPULAR_SEARCH_KEY = "popular_searches";
    private static final String SEARCH_KEY = "search_autocomplete";
    private static final String RECENT_SEARCH_KEY = "search_autocomplete";
    private final RedisTemplate<String, Object> redisTemplate;

// 인덱싱, 클린코딩

    public Page<Product> search(SearchDTO searchDTO){
        Pageable pageable = PageRequest.of(searchDTO.getPagingIndex(),searchDTO.getPagingSize());
        Specification<Product> spec = (root, query, criteriaBuilder) -> null;
        if (searchDTO.getKeyword() != null) { // 키워드 검색
            spec = spec.and(ProductSpecification.searchKeyword(searchDTO.getKeyword()));
        }
        if(searchDTO.getBrand() != null)
            spec = spec.and(ProductSpecification.equalBrand(searchDTO.getBrand()));
        if(searchDTO.getCategory() != null)
            spec = spec.and(ProductSpecification.equalCategory(searchDTO.getCategory()));
        if(searchDTO.getColor() != null)
            spec = spec.and(ProductSpecification.equalColor(searchDTO.getColor()));
        if(searchDTO.getSort() != null)
            spec = spec.and(ProductSpecification.sort(searchDTO.getSort()));
        Page<Product> list = productRepository.findAll(spec, pageable);
        if(!list.isEmpty()&&searchDTO.getKeyword() != null){
            redisTemplate.opsForZSet().incrementScore(POPULAR_SEARCH_KEY, searchDTO.getKeyword(), 1);// 인기 검색어 선정을 위한
        }
        return list;
    }

    public List<String> getPopularSearches() {
        Set<ZSetOperations.TypedTuple<Object>> popularSearches = redisTemplate.opsForZSet().reverseRangeWithScores(POPULAR_SEARCH_KEY, 0, 10);
        return popularSearches.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .map(Object::toString)
                .collect(Collectors.toList());
    }
    // redis set에 subtitle 저장
    public void saveSearchKeyword() {
        List<ProductMapping> productList = productRepository.findAllBy();
        List<String> subTitleList = new ArrayList<>();

        for (ProductMapping product : productList) {
                subTitleList.add(product.getProductSubTitle());
        }
        for (String str : subTitleList) {
            redisTemplate.opsForZSet().add(SEARCH_KEY, str, 1);
        }
    }

    public Set<Object> getSearchSuggestions(String keword, int cnt) {
        Range range = Range.range().gte(keword).lte(keword + '\uffff');
        Limit limit = Limit.limit().count(cnt); // 최대 결과 수
        return redisTemplate.opsForZSet().rangeByLex(SEARCH_KEY, range, limit);
    }


}
