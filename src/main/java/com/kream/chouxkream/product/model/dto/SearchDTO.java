package com.kream.chouxkream.product.model.dto;

import com.kream.chouxkream.user.model.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SearchDTO {
    //like문으로 처리
    private String keyword;

    private List<Integer> category;

    private List<String> color;

    private List<String> brand;

    private String sort;
}