package com.kream.chouxkream.bid.repository;

import com.kream.chouxkream.bid.model.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query("SELECT MIN(b.bidPrice) FROM Bid b WHERE b.productSize.productSizeNo = :productSizeNo AND b.bidType = 'sell'")
    Integer findMinBidPriceByProductSizeNo(Long productSizeNo);

    @Query("SELECT MAX(b.bidPrice) FROM Bid b WHERE b.productSize.productSizeNo = :productSizeNo AND b.bidType = 'buy'")
    Integer findMaxBidPriceByProductSizeNo(Long productSizeNo);
}
