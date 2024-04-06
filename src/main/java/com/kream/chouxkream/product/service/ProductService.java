package com.kream.chouxkream.product.service;

import com.kream.chouxkream.product.ProductSpecification;
import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private static final String POPULAR_SEARCH_KEY = "popular_searches";
    private static final String SEARCH_KEY = "search_autocomplete";
    private final RedisTemplate<String, Object> redisTemplate;

// 1 .페이징 2. 정렬 3. 인덱싱 4. 자동완성 기능을 sub_title로 구현하기 5. 검색 기능 파라미터 url로 돌리기 6. 클린코딩 7. 검색어가 없을 때 저장되지 않도록 처리

    public List<Product> search(SearchDTO searchDTO){
        Specification<Product> spec = (root, query, criteriaBuilder) -> null;
        if (searchDTO.getKeyword() != null) { // 키워드 검색
            spec = spec.and(ProductSpecification.searchKeyword(searchDTO.getKeyword()));
            redisTemplate.opsForZSet().incrementScore(POPULAR_SEARCH_KEY, searchDTO.getKeyword(), 1); // 인기 검색어 선정을 위한
            saveSearchKeyword(searchDTO.getKeyword()); // 검색어 자동완성을 위한 데이터(검색어) 저장
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

    public void saveSearchKeyword(String keyword) {
        redisTemplate.opsForZSet().incrementScore(SEARCH_KEY, keyword, 1);
    }

    public Set<Object> getSearchSuggestions(String prefix) {
        Range range = Range.range().gte(prefix).lte(prefix + '\uffff');
        Limit limit = Limit.limit().count(5); // 최대 결과 수
        return redisTemplate.opsForZSet().rangeByLex(SEARCH_KEY, range, limit);
    }
}
