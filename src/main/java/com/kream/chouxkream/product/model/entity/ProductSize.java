package com.kream.chouxkream.product.model.entity;

import com.kream.chouxkream.user.model.entity.Wishlist;
import com.kream.chouxkream.bid.model.entity.Bid;
import lombok.*;

import javax.persistence.*;
import java.util.List;
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

    @OneToMany(mappedBy = "productSize")// 하나의 productsize객체가 여러 wishlist를 참조할수 있다.
    private List<Wishlist> wishlist;

    @Builder.Default
    @OneToMany(mappedBy = "productSize", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Set<Bid> bids = new HashSet<>();
}
