package com.kream.chouxkream.product.service;

import com.kream.chouxkream.bid.repository.BidRepository;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductImagesRepository;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.repository.ProductSizeRepositroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.kream.chouxkream.product.repository.ProductSpecification;
import com.kream.chouxkream.product.model.dto.SearchDTO;
import com.kream.chouxkream.product.model.dto.SearchResultDTO;
import com.kream.chouxkream.product.repository.ProductMapping;
import com.kream.chouxkream.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSizeRepositroy productSizeRepositroy;
    private final ProductImagesRepository productImagesRepository;
    private final BidRepository bidRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POPULAR_SEARCH_KEY = "popular_searches";
    private static final String SEARCH_KEY = "search_autocomplete";
    private static final String RECNET_SEARCH_KEY = "recent_searches";
    private static final int RECENT_SEARCH_KEY_SIZE = 10;

    @Transactional
    public Optional<Product> getProductById(Long productNo) {

        return productRepository.findById(productNo);
    }

    @Transactional
    public List<ProductSize> getProductSizeById(Long productNo) {

        return productSizeRepositroy.findByProductNo(productNo);
    }

    @Transactional
    public List<String> getProductImageUrlByProductNo(Long productNo) {

        return productImagesRepository.findImageUrlListByProductNo(productNo);
    }


    @Transactional
    public Integer getMinSellPrice(Long productSizeNo) {

        return bidRepository.findMinBidPriceByProductSizeNo(productSizeNo);
    }

    @Transactional
    public Integer getMaxBuyPrice(Long productSizeNo) {

        return bidRepository.findMaxBidPriceByProductSizeNo(productSizeNo);
    }

    public Page<SearchResultDTO> search(SearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(searchDTO.getPagingIndex()-1,searchDTO.getPagingSize());
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

        return list.map(product -> {
            List<String> imageUrls = productImagesRepository.findImageUrlListByProductNo(product.getProductNo());
            List<String> productImageUrls = (imageUrls.isEmpty()) ? Collections.emptyList() : imageUrls;

            return new SearchResultDTO(
                    product.getProductNo(),
                    product.getProductTitle(),
                    product.getProductSubTitle(),
                    product.getReleasePrice(),
                    product.getBrand().getBrandName(),
                    productImageUrls
            );
        });
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

    public Set<Object> getSearchSuggestions(String keyword, int cnt) {

        Range range = Range.range().gte(keyword).lte(keyword + '\uffff');
        Limit limit = Limit.limit().count(cnt); // 최대 결과 수

        return redisTemplate.opsForZSet().rangeByLex(SEARCH_KEY, range, limit);
    }

    public void saveRecentSearches(String email, String keyword) {

        // to Do 1. user 확인
        String key = RECNET_SEARCH_KEY+email;
        Long size = redisTemplate.opsForList().size(key);
        if(size == (long) RECENT_SEARCH_KEY_SIZE){
            redisTemplate.opsForList().rightPop(key);
        }
        redisTemplate.opsForList().leftPush(key, keyword);
        System.out.println("size : " + size);
        System.out.println(email);
        System.out.println(keyword);
    }

    public List<Object> getRecentSearches(String email) {

        String key = RECNET_SEARCH_KEY+email;
        Long size = redisTemplate.opsForList().size(key);
        System.out.println("size : "+size);
        System.out.println(key);
        long endIndex = Math.min(size - 1, RECENT_SEARCH_KEY_SIZE - 1);

        return redisTemplate.opsForList().range(key, 0, endIndex);
    }
}
