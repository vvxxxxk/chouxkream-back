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
//        String email = authentication.getName();
        String email = "ee121111@test.com";

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
    @PostMapping("/buy/{product_no}") // 바로구매 -> 최저입찰가 등록, 일반구매 -> 입찰가 선택해서 입찰 등록 : 둘다 암튼 입찰등록
    public ResponseEntity<ResponseMessageDto> addBidForBuying(@PathVariable("product_no") Long productNo,
                                                              @RequestParam("product_size_no") Long productSizeNo,
                                                              @RequestBody PurchaseDto purchaseDto) { //q배송지, 포인트사용여부,,새배송지추가된거 비드디티오? 페이먼트디티오?
        // 펄처스 디티오에 ㅠ페이먼트 스트링으로 받아오고 비드도 받아와서, 밑에 코드에서 디비에저장하고, 펄쳐스 디티오 리스폰스해서
        //프론트에서 결제페이지 로드하도록.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //        String email = authentication.getName();
        String email = "ee121111@test.com";

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        ProductSize productSize =  productSizeService.getProductSizeByNo(productSizeNo);//구매입찰 에선 재고체크 X.
        Bid newBid = bidService.addBid(optionalUser.get(), productSize, purchaseDto.getPrice());
//        PurchaseDto newPurchaseDto = new PurchaseDto(optionalUser.get().getUserNo(), newBid.getBidNo(), purchaseDto.getAddressNo(), purchaseDto.getPaymentNo(), purchaseDto.getPrice());


        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
//        responseMessageDto.addData("purchaseInfo", newPurchaseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

    }


}
