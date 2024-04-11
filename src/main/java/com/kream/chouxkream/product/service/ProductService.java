package com.kream.chouxkream.product.service;

import com.kream.chouxkream.bid.repository.BidRepository;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.repository.ProductImagesRepository;
import com.kream.chouxkream.product.repository.ProductRepositroy;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.repository.ProductSizeRepositroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepositroy productRepositroy;
    private final ProductSizeRepositroy productSizeRepositroy;
    private final ProductImagesRepository productImagesRepository;
    private final BidRepository bidRepository;

    @Transactional
    public Optional<Product> getProductById(Long productNo) {

        return productRepositroy.findById(productNo);
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
}
