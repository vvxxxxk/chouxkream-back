package com.kream.chouxkream.bid.model.entity;

import com.kream.chouxkream.bid.model.dto.BidStatus;
import com.kream.chouxkream.bid.model.dto.BidType;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.user.model.entity.User;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidType bidType;

    @Column(nullable = false)
    private int bidPrice;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus bidStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_size_no")
    private ProductSize productSize;

//    @OneToOne(mappedBy = "bid", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
//    private Payment payment;
}

