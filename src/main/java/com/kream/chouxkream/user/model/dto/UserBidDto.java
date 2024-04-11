package com.kream.chouxkream.user.model.dto;

import com.kream.chouxkream.bid.model.dto.BidStatus;
import com.kream.chouxkream.bid.model.dto.BidType;
import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.model.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UserBidDto {

    // Bid Info
    private Long bidNo;
    private BidType bidType;
    private int bidPrice;
    private Timestamp bidCreateDate;
    private BidStatus bidStatus;

    // Product Info
    private Long productNo;
    private String productTitle;
    private String productSubTitle;

    // ProductSize Info
    private Long productSizeNo;
    private String sizeName;

    // ProductImage Info
    private String imageUrl;

    public void setBidInfo(Bid bid) {
        this.bidNo = bid.getBidNo();
        this.bidType = bid.getBidType();
        this.bidPrice = bid.getBidPrice();
        this.bidCreateDate = bid.getCreateDate();
        this.bidStatus = bid.getBidStatus();
    }

    public void setProductInfo(Product product) {
        this.productNo = product.getProductNo();
        this.productTitle = product.getProductTitle();
        this.productSubTitle = product.getProductSubTitle();
    }

    public void setProductSizeInfo(ProductSize productSize) {
        this.productSizeNo = productSize.getProductSizeNo();
        this.sizeName = productSize.getSizeName();
    }

    public void setProductImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
