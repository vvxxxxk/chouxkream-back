package com.kream.chouxkream.bid.repository;

import com.kream.chouxkream.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
