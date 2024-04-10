package com.kream.chouxkream.product.model.entity;

import com.kream.chouxkream.brand.model.entity.Brand;
import com.kream.chouxkream.category.model.entity.Category;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Timestamp;

@Entity
@Getter
@Setter // 컬럼 당 setter 배정
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productNo;

    @Column(nullable = false)
    private String productTitle;

    @Column(nullable = false)
    private String productSubTitle;

    @Column(nullable = false)
    private String modelId;

    @Column(nullable = true)
    private String productColor;

    @Column(nullable = true)
    private Integer releasePrice;

    @Column(nullable = true)
    private Timestamp releaseDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp createDate;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updateDate;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int viewsCount;

    @Column(nullable = true)
    private String metaKeyword;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name="brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name="category_no")
    private Category category;
}
