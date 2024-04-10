package com.kream.chouxkream.user.model.entity;

import com.kream.chouxkream.productsize.model.entity.ProductSize;
import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistNo;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private User user;

    @OneToMany(mappedBy = "wishlist", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<ProductSize> productSizeSet;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private Timestamp createDate;

}
