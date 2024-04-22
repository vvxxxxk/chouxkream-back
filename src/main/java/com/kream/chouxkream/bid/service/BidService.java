package com.kream.chouxkream.bid.service;

import com.kream.chouxkream.bid.model.dto.BidStatus;
import com.kream.chouxkream.bid.model.dto.BidType;
import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.bid.repository.BidRepository;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.service.ProductService;
import com.kream.chouxkream.product.service.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.kream.chouxkream.user.model.entity.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;


    public Bid addBuyBid(User user, ProductSize productSize, int price) {
        Bid bid = Bid.builder()
                .bidType(BidType.buy)
                .bidPrice(price)
                .createDate(Timestamp.valueOf(LocalDateTime.now()))
                .bidStatus(BidStatus.bid_progress)
                .user(user)
                .productSize(productSize)
                .build();

        return this.bidRepository.save(bid);
    }

    public Bid getBidByBidNo(Long bidNo) {
        return this.bidRepository.findById(bidNo)
                .orElse(null);
    }

    public Bid addSellBId(User user, ProductSize productSize, int price) {
        Bid bid = Bid.builder()
                .bidType(BidType.sell)
                .bidPrice(price)
                .createDate(Timestamp.valueOf(LocalDateTime.now()))
                .bidStatus(BidStatus.bid_progress)
                .user(user)
                .productSize(productSize)
                .build();

        return this.bidRepository.save(bid);
    }
}
