package com.kream.chouxkream.bid.repository;

import com.kream.chouxkream.bid.model.dto.BidStatus;
import com.kream.chouxkream.bid.model.dto.BidType;
import com.kream.chouxkream.bid.model.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query("SELECT MIN(b.bidPrice) FROM Bid b WHERE b.productSize.productSizeNo = :productSizeNo AND b.bidType = 'sell'")
    Integer findMinBidPriceByProductSizeNo(Long productSizeNo);

    @Query("SELECT MAX(b.bidPrice) FROM Bid b WHERE b.productSize.productSizeNo = :productSizeNo AND b.bidType = 'buy'")
    Integer findMaxBidPriceByProductSizeNo(Long productSizeNo);

    @Query("SELECT b FROM Bid b WHERE b.user.userNo = :userNo AND b.bidType = 'buy' AND b.bidStatus != 'bid_delete'")
    List<Bid> findBuyBidListByUserNo(Long userNo);

    Page<Bid> findByUserUserNoAndBidTypeAndBidStatusNot(Long userNo, BidType bidType, BidStatus bidStatus, Pageable pageable);

    Page<Bid> findByUserUserNoAndBidTypeAndBidStatusNotAndCreateDateBetween(Long userNo, BidType bidType, BidStatus bidStatus, Date startDate, Date endDate, Pageable pageable);

}
