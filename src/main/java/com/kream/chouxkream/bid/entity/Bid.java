package com.kream.chouxkream.bid.entity;

import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidNo;

    @Column(nullable = false)
    private Long userNo;

    @Column(nullable = false)
    private Long productSizeNo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BidType bidType;

    @Column(nullable = false)
    private int bidPrice;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private Timestamp createDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BidStatus bidStatus;

}
