package com.kream.chouxkream.product;

import com.kream.chouxkream.brand.Brand;
import com.kream.chouxkream.category.Category;
import com.kream.chouxkream.productimages.ProductImages;
import com.kream.chouxkream.productsize.ProductSize;
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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productNo;

    @Column(nullable = false)
    private String productTitle;

    @Column(nullable = false)
    private String productSubTitle;

    @Column(nullable = true)
    private String modelId;

    @Column(nullable = true)
    private String productColor;

    @Column(nullable = true)
    private Integer releasePrice;

    @Column(nullable = true)
    private Timestamp releaseDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updateDate;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int viewsCount;

    @Column(nullable = true)
    private String metaKeyword;

    @Column(nullable = true)
    private String metaDesc;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_no")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ProductImages> productImages = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ProductSize> productSizes = new HashSet<>();
}
