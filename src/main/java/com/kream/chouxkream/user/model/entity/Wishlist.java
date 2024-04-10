package com.kream.chouxkream.user.model.entity;

import com.kream.chouxkream.productsize.ProductSize;
import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishListNo;

    @OneToOne
    @JoinColumn(name = "user_no")
    private User user;

    @OneToMany(mappedBy = "wishlist", cascade = {CascadeType.MERGE, CascadeType.PERSIST}) // 한개의 고유한 wishlist 객체가 여러개의 productSize 객체를 참조할 수 있다.
    private Set<ProductSize> productSizeSet;
    //주인을 생각하고..주인인ㅇ애를 many로 두면된다.. child가 주인.. 부모가 없어지면 자동으로 소멸되는 애 쪽이 주인..
    // 위시리스트랑 프로덕트사이에는 누가부모/ 위시리스트ㅏㄱ 부모 . 고로 프로덕트가 주인. 프로덕트가 many


    @Column(columnDefinition = "timestamp default current_timestamp")
    private Timestamp createDate;

}
