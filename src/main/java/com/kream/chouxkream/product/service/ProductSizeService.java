package com.kream.chouxkream.product.service;

import com.kream.chouxkream.product.model.dto.ProductSizeDto;
import com.kream.chouxkream.product.model.entity.ProductImages;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.repository.ProductSizeRepository;
import com.kream.chouxkream.user.model.entity.Wishlist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ProductSizeService {
    private final ProductSizeRepository productSizeRepository;

    public ProductSize getProductSizeByNo(Long productSizeNo) {
        return this.productSizeRepository.findById(productSizeNo).get();
    }

    @Transactional
    public List<ProductSize> getMyWishListProductSizes (List<Wishlist> myWishList) {
        List<ProductSize> myWishProductSizeList = new ArrayList<>();
        for (Wishlist w : myWishList) {
            myWishProductSizeList.add(w.getProductSize());
        }
        return myWishProductSizeList;
    }

    @Transactional
    public Page<ProductSize> getPagedProductSizes(List<ProductSize> productSizeList, PageRequest pageRequest) {
        List<Long> productSizeNoList = new ArrayList<>();
        for (ProductSize p : productSizeList) {
            productSizeNoList.add(p.getProductSizeNo());
        }
        return productSizeRepository.findByProductSizeNoIn(productSizeNoList,pageRequest);
    }

    @Transactional
    public List<ProductSizeDto> setProductSizeDtoList(Page<ProductSize> pagingProductSize) {
        List<ProductSizeDto> productSizeDtoList = new ArrayList<>();



        for (ProductSize p : pagingProductSize) {
            ProductSizeDto productSizeDto = new ProductSizeDto();
            productSizeDto.setProductSizeNo(p.getProductSizeNo());
            productSizeDto.setSizeName(p.getSizeName());

            Set<ProductImages> productImagesSet = p.getProduct().getProductImages();
            Optional<ProductImages> optionalProductImages = productImagesSet.stream().findFirst();
            String imageUrl = null;
            if (optionalProductImages.isPresent()) {
                imageUrl = optionalProductImages.get().getImageUrl();
            }
            productSizeDto.setImageUrl(imageUrl);


            productSizeDtoList.add(productSizeDto);


        }
        return productSizeDtoList;
    }

    @Transactional
    public ProductSizeDto setProductSizeDto(ProductSize productSize) {
        ProductSizeDto productSizeDto = new ProductSizeDto();
        productSizeDto.setProductSizeNo(productSize.getProductSizeNo());
        productSizeDto.setSizeName(productSize.getSizeName());

        Set<ProductImages> productImagesSet = productSize.getProduct().getProductImages();
        Optional<ProductImages> optionalProductImages = productImagesSet.stream().findFirst();
        String imageUrl = null;
        if (optionalProductImages.isPresent()) {
            imageUrl = optionalProductImages.get().getImageUrl();
        }
        productSizeDto.setImageUrl(imageUrl);
        return productSizeDto;
    }

    public ProductSize getProductSizeByProductNoAndSizeName(Long productNo, String sizeName) {
        return this.productSizeRepository.findByProductProductNoAndSizeName(productNo, sizeName)
                .orElse(null);
    }
}
