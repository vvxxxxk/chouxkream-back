package com.kream.chouxkream.product;

import com.kream.chouxkream.brand.model.entity.Brand;
import com.kream.chouxkream.product.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.function.Predicate;


public class ProductSpecification {
    public static Specification<Product> searchKeyword(String keyword) {
        return (root, query, builder) -> {
            if (StringUtils.isEmpty(keyword)) {
                // 검색 키워드가 비어 있는 경우 항상 false를 반환하여 데이터가 출력되지 않도록 합니다.
                return builder.disjunction();
            } else {
                Expression<String> concatenatedColumns = builder.function(
                        "concat",
                        String.class,
                        root.get("title"),
                        builder.literal(" "),
                        root.get("subTitle"),
                        builder.literal(" "),
                        root.get("modelId")
                        // 필요에 따라 추가 컬럼들을 계속 추가할 수 있습니다.
                );
                return builder.like(concatenatedColumns, "%" + keyword + "%");
            }
        };
    }

    //컬러를 통한 검색
    public static Specification<Product> equalColor(List<String> colors) {
        return (root, query, criteriaBuilder) -> {
            return root.get("color").in(colors);
        };
    }

    public static Specification<Product> equalBrand(List<String> brands) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Brand> brandJoin = root.join("brand", JoinType.INNER);
            return brandJoin.get("brandId").in(brands);
        };
    }

    public static Specification<Product> equalCategory(List<Integer> categorys) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Brand> brandJoin = root.join("category", JoinType.INNER);
            return brandJoin.get("categoryNo").in(categorys);
        };
    }
}
