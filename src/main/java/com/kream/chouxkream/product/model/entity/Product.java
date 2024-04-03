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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productNo;

    @Column(nullable = false, name = "product_title")
    private String title;

    @Column(nullable = false, name = "product_sub_title")
    private String subTitle;

    @Column(nullable = false)
    private String modelId;

    @Column(nullable = true, name="product_color")
    private String color;

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

    @OneToOne
    @JoinColumn(name="brand_id")
    private Brand brand;

    @OneToOne
    @JoinColumn(name="category_no")
    private Category category;
}
