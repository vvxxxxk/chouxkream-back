package com.kream.chouxkream.productsize.model.entity;

import com.kream.chouxkream.product.model.entity.Product;
import com.kream.chouxkream.user.model.entity.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @ManyToOne
    @JoinColumn(name = "product_no")
    private Product product;

    @ManyToOne
    private Wishlist wishlist;

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }
}
