package com.kream.chouxkream.bid.controller;

import com.kream.chouxkream.bid.model.dto.PurchaseDto;
import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.bid.service.BidService;
import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.product.model.dto.ProductDetailDto;
import com.kream.chouxkream.product.model.dto.ProductSizeDto;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.service.ProductService;
import com.kream.chouxkream.product.service.ProductSizeService;
import com.kream.chouxkream.user.model.dto.AddressDto;
import com.kream.chouxkream.user.model.dto.UserInfoDto;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.AddressService;
import com.kream.chouxkream.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;
    private final UserService userService;
    private final ProductService productService;
    private final ProductSizeService productSizeService;
    private final AddressService addressService;

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

        ProductSize productSize =  productSizeService.getProductSizeByProductNoAndSizeName(productNo, sizeName);
        ProductSizeDto productSizeDto = productSizeService.setProductSizeDto(productSize); // sizeName
        ProductDetailDto productDetailDto = new ProductDetailDto(productSize.getProduct()); // modelId

        UserInfoDto userInfoDto = new UserInfoDto(optionalUser.get());
        List<AddressDto> addressDtoList = addressService.setAddressDtoList(optionalUser.get().getAddresses());


        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("user", userInfoDto);
        responseMessageDto.addData("userAddressList", addressDtoList);
        responseMessageDto.addData("productSize", productSizeDto);
        responseMessageDto.addData("productDetail",productDetailDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "구매 입찰 등록")
    @PostMapping("/buy/{product_no}")
    public ResponseEntity<ResponseMessageDto> addBidForBuying(@PathVariable("product_no") Long productNo,
                                                              @RequestParam("product_size_no") Long productSizeNo,
                                                              @RequestBody PurchaseDto purchaseDto) { // 배송지추가 -> addresscontroller에 배송지추가 api재사용 하기

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        ProductSize productSize =  productSizeService.getProductSizeByNo(productSizeNo);//구매입찰 에선 재고체크 X.
        bidService.addBuyBid(optionalUser.get(), productSize, purchaseDto.getPrice()); // 판매 bid등록. --> 추후 purchase와 연결할지 고민중.
        PurchaseDto purchaseDtoForPayment = new PurchaseDto(optionalUser.get().getUserNo(), purchaseDto.getAddressNo() , purchaseDto.getPaymentMethod(), purchaseDto.getPrice(), purchaseDto.getPointToUse());


        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("purchaseInfoForPayment", purchaseDtoForPayment);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

    }



}
