package com.kream.chouxkream.productsize;

import com.kream.chouxkream.product.Product;
import com.kream.chouxkream.user.model.entity.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "productSize")// 하나의 productsize객체가 여러 wishlist를 참조할수 있다.
    private List<Wishlist> wishlist;

//    public void setWishlist(Wishlist wishlist) {
//        this.wishlist = wishlist;
//    }
//
//    public Wishlist getWishlist() {
//        return wishlist;
//    }
}
