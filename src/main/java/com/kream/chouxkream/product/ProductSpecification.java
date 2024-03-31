package com.kream.chouxkream.product;

import com.kream.chouxkream.product.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.function.Predicate;


public class ProductSpecification {
    public static Specification<Product> likeKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), '%' + keyword + '%');
    }
}
