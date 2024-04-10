package com.kream.chouxkream.user.controller;

import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.productsize.model.entity.ProductSize;
import com.kream.chouxkream.productsize.model.entity.ProductSizeDto;
import com.kream.chouxkream.user.ResourceNotFoundException;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.Wishlist;
import com.kream.chouxkream.user.service.UserService;
import com.kream.chouxkream.user.service.WishlistService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final UserService userService;

    @ApiOperation(value = "관심 상품 조회")
    @GetMapping
    public ResponseEntity<ResponseMessageDto> getWishLists (){
        String email = getSiteUserEmail();
//        String email = "ee121111@test.com";
        try {

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("not found user"));

            Set<Wishlist> myWishList = user.getWishlistSet();
//          wishlist 에 productsize들 각각 이름,사진 가격 꺼내와야함 String으로?


            StatusCode statusCode = StatusCode.FIND_USER_SUCCESS;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            //프로덕트사이즈리스폰스해야댐ㅅㅂ 디티오로?
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } catch(ResourceNotFoundException ex) {
            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "관심 상품 등록/해제")
    @PostMapping
    public ResponseEntity<ResponseMessageDto> addOrDeleteWishList(@RequestBody ProductSizeDto productSizeDto){ //토글//product Size No?받아와야함.
        //if userno가 wishlist에 잇으면 해제. 없으면 등록.
        String userEmail = getSiteUserEmail();
        try {
            boolean isExists;
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("not found user"));

            ProductSize productSize = productsizeservice.getbyNo(productSizeDto.getNo);

            wishlistService.updateWishlist(user, productSize);

        } catch () {

        }


    }

    private String getSiteUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    private ResponseMessageDto setResponseMessageDto (StatusCode statusCode) {
        ResponseMessageDto responseMessageDto = new ResponseMessageDto();
        responseMessageDto.setCode(statusCode.getCode());
        responseMessageDto.setMessage(statusCode.getMessage());
        return responseMessageDto;
    }
}
