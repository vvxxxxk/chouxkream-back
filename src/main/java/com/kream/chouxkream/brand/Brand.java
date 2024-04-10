package com.kream.chouxkream.brand;

import com.kream.chouxkream.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    private String brandId;

    @Column(nullable = false)
    private String brandName;

    @Column(nullable = true)
    private String brandSubName;

    @Column(nullable = true)
    private String brandLogoUrl;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updateDate;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isActive;

    @OneToMany(mappedBy = "brand", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Product> products = new HashSet<>();
}
