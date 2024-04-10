package com.kream.chouxkream.product;

import com.kream.chouxkream.brand.model.entity.Brand;
import com.kream.chouxkream.product.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.function.Predicate;


public class ProductSpecification {
    //키워드를 통한 검색
    public static Specification<Product> searchKeyword(String keyword) {
        return (root, query, builder) -> {
            if (StringUtils.isEmpty(keyword)) {
                // 검색 키워드가 비어 있는 경우 항상 false를 반환, 데이터가 출력되지 않음
                return builder.disjunction();
            } else {
                Expression<String> concatenatedColumns = builder.function(
                        "concat",
                        String.class,
                        // title, subtitle, modelId 에서 검색 진행
                        root.get("productTitle"),
                        builder.literal(" "),
                        root.get("productSubTitle"),
                        builder.literal(" "),
                        root.get("modelId")
                );
                return builder.like(concatenatedColumns, "%" + keyword + "%"); // like문 활용해 keyword 포함한 상품 탐색
            }
        };
    }

    //컬러를 통한 검색, 여러개의 색깔을 검색을 위해 배열로 받음
    public static Specification<Product> equalColor(List<String> colors) {
        return (root, query, criteriaBuilder) -> {
            return root.get("productColor").in(colors);
        };
    }

    //브랜드 검색
    public static Specification<Product> equalBrand(List<String> brands) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Brand> brandJoin = root.join("brand", JoinType.INNER); // 브랜드와 상품 테이블 조인
            return brandJoin.get("brandId").in(brands);// 조인한 객체에서 브랜드 Id에 접근
        };
    }

    //카테고리 검색
    public static Specification<Product> equalCategory(List<Integer> categorys) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Brand> brandJoin = root.join("category", JoinType.INNER);
            return brandJoin.get("categoryNo").in(categorys);
        };
    }

    //정렬 추가
    public static Specification<Product> sort(String sort) {
        return (root, query, criteriaBuilder) -> {
            if ("pNo".equals(sort)) {
                query.orderBy(criteriaBuilder.asc(root.get("productNo")));
            } else if ("pNoDesc".equals(sort)) {
                query.orderBy(criteriaBuilder.desc(root.get("productNo")));
            }
            return null;
        };
    }


}
