package com.kream.chouxkream.user.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressNo;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_no")
    private User user;

    @Setter
    @Column(nullable = false)
    private String receiverName;

    @Setter
    @Column(nullable = false)
    private String receiverPhone;

    @Setter
    @Column(nullable = false)
    private String zipcode;

    @Setter
    @Column(nullable = false)
    private String address;

    @Setter
    @Column(nullable = true)
    private String detailAddress;

    @Setter
    private boolean defaultAddress;

    public void unFilleddetailAddress() {
        this.detailAddress = "Unfilled address details.";
    }

//    public Address(String receiverName, String receiverPhone, String zipcode, String address, boolean defaultAddress) {
//        this.receiverName = receiverName;
//        this.receiverPhone = receiverPhone;
//        this.zipcode = zipcode;
//        this.address = address;
//        this.defaultAddress = defaultAddress;
//    }

}
