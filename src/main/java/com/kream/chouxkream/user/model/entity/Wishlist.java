package com.kream.chouxkream.user.model.entity;

import com.kream.chouxkream.productsize.ProductSize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistNo;


    @ManyToOne
    @JoinColumn(name = "user_no")
    private User user;


    @ManyToOne
    @JoinColumn(name = "product_size_no")
    private ProductSize productSize;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private Timestamp createDate;


}
