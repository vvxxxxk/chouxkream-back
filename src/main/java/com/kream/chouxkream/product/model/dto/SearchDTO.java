package com.kream.chouxkream.product.model.dto;

import com.kream.chouxkream.user.model.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class SearchDTO {
    //like문으로 처리
    private String keyword;

    private int categoryId;

    private String color;

    private String brand;

    private String sort;
}