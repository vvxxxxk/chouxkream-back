package com.kream.chouxkream.bid.controller;

import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.user.ResourceNotFoundException;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buy")
public class BidController {
//    private final BidService bidService;
    private final UserService userService;
    @ApiOperation(value = "상품 구매")
    @GetMapping("/{productNo}")
    public ResponseEntity<ResponseMessageDto> buyProduct(@PathVariable("product_no") Long productNo) {

//        회원인증부터해야함

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
        String email = "test@test.com";
        User user = userService.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("login necessary"));
        try {

        }catch (ResourceNotFoundException ex) {

        }


        //product_size 테이블에서 product_no컬럼값이 productNo와 일치하는거 반환?/product_size_no(pk)리스트.로.
        // 그 중 .. 위의 반한 pk리스트 일치하는거 bid테이블의 product_size_no컬럽값과 일치하는것들 리스트 반환
        // 위 리스트, product_size_no 별로 분류 하고, 각 분류별 price최저가만 추출하여 반환.

    }
}
