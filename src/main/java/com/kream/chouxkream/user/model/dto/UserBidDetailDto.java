package com.kream.chouxkream.user.model.dto;

import com.kream.chouxkream.bid.model.dto.BidStatus;
import com.kream.chouxkream.bid.model.dto.BidType;
import com.kream.chouxkream.bid.model.dto.PaymentMethod;
import com.kream.chouxkream.bid.model.dto.PaymentStatus;
import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.bid.model.entity.Payment;
import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.user.model.entity.Address;
import com.kream.chouxkream.user.model.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
public class UserBidDetailDto {

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

    // Address Info
    private Long addressNo;
    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String address;
    private String detailAddress;

    // Payment Info
    private Long paymentNo;
    private PaymentMethod paymentMethod;
    private int paymentPrice;
    private Timestamp paymentDate;
    private PaymentStatus paymentStatus;

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

    public void setAddressInfo(Address address) {
        this.addressNo = address.getAddressNo();
        this.receiverName = address.getReceiverName();
        this.receiverPhone = address.getReceiverPhone();
        this.zipcode = address.getZipcode();
        this.address = address.getAddress();
        this.detailAddress = address.getDetailAddress();
    }

    public void setPaymentInfo(Payment payment) {
        this.paymentNo = payment.getPaymentNo();
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentPrice = payment.getPaymentPrice();
        this.paymentDate = payment.getPaymentDate();
        this.paymentStatus = payment.getPaymentStatus();
    }
}
