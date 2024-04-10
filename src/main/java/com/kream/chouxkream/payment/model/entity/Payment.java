package com.kream.chouxkream.payment.model.entity;

import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.payment.model.dto.PaymentMethod;
import com.kream.chouxkream.payment.model.dto.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private int paymentPrice;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp on update current_timestamp")
    private Timestamp paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @OneToOne
    @JoinColumn(name = "bid_no")
    private Bid bid;
}
