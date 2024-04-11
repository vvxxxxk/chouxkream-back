package com.kream.chouxkream.product.model.entity;

import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.product.model.entity.Product;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productSizeNo;

    @Column(nullable = false)
    private String sizeName;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int sellCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no")
    private Product product;

    @Builder.Default
    @OneToMany(mappedBy = "productSize", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Bid> bids = new HashSet<>();
}
