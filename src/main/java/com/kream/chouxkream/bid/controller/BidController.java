package com.kream.chouxkream.bid.controller;

import com.kream.chouxkream.bid.service.BidService;
import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.service.ProductService;
import com.kream.chouxkream.product.service.ProductSizeService;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;
    private final UserService userService;
    private final ProductService productService;
    private final ProductSizeService productSizeService;

    @ApiOperation(value = "구매 입찰가 작성 폼")
    @GetMapping("/buy/{product_no}")
    public ResponseEntity<ResponseMessageDto> getBuyBidFromData(@PathVariable("product_no") Long productNo,
                                                                @RequestParam("size_name") String sizeName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
//        System.out.println("productNo = " + productNo);
//        System.out.println("productSizeNo = " + productSizeNo);

        ProductSize productSize =  productSizeService.getProductSizeByProductNoAndSizeName(productNo, sizeName); //사이즈만 보내주고
//
//        productSize.getProduct(). -> 이미지, 모델명,
//        productSize. -> 사이즈명
//        회원정보, 회원 기본 배송지정보;
//        StatusCode statusCode = StatusCode.SUCCESS;
//        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
//        responseMessageDto.addData("product", productDetailDto);
//        responseMessageDto.addData("size", productSizeDetailDtoList);
//        responseMessageDto.addData("images", productImageUrlByProductNo);
//        responseMessageDto.addData("sell_info", sellPriceList);
//        responseMessageDto.addData("buy_info", buyPriceList);
// response에 회원정봐 배송지정보 보냏야함
//        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        return null;
    }


    @ApiOperation(value = "구매 입찰 등록")
    @PostMapping("/{product_no}")
    public ResponseEntity<ResponseMessageDto> addBidForBuying(@PathVariable("product_no") Long productNo,
                                                              @RequestParam("size") String sizeName,
                                                              @RequestParam("type") String bidType,
                                                              @RequestParam("price") int price) { // 사이즈와 , buy 를 받아와야함.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }


        ProductSize productSize =  productSizeService.getProductSizeByProductNoAndSizeName(productNo, sizeName); //구매입찰 에선 재고체크 X.
        if (bidType.equals("buy")) {
            bidService.addBid(optionalUser.get(), productSize, price);
        }


        return null;

    }


}
