package com.kream.chouxkream.product.model.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchDTO {

    //like문으로 처리
    private String keyword;

    private List<Integer> category;

    private List<String> color;

    private List<String> brand;

    private String sort;

    private Integer pagingIndex = 1;

    private Integer pagingSize = 1;

    private String userId;
}