package com.kream.chouxkream.user.controller;

import com.kream.chouxkream.bid.model.entity.Bid;
import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.product.model.dto.ProductSizeDto;
import com.kream.chouxkream.product.model.entity.ProductSize;
import com.kream.chouxkream.product.service.ProductSizeService;
import com.kream.chouxkream.product.service.ProductService;
import com.kream.chouxkream.user.model.dto.*;
import com.kream.chouxkream.user.model.entity.Address;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.Wishlist;
import com.kream.chouxkream.user.service.AddressService;
import com.kream.chouxkream.user.service.UserService;
import com.kream.chouxkream.user.service.WishlistService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ProductSizeService productSizeService;
    private final AddressService addressService;
    private final WishlistService wishlistService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<ResponseMessageDto> join(@Valid @RequestBody UserJoinDto userJoinDto) {

        // 이메일 인증 체크
        // 이메일 미인증 시 4007 상태코드 반환
        if (!userJoinDto.isEmailAuth()) {

            StatusCode statusCode = StatusCode.MISSING_REQUIRED_FIELD;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 이메일 중복 체크
        // 중복된 이메일인 경우 4013 상태코드 반환
        boolean isEmailDuplicate = userService.isEmailExists(userJoinDto.getEmail());
        if (isEmailDuplicate) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 회원가입
        // 회원가입 성공 시 2000 상태코드 반환
        userService.join(userJoinDto);
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "인증 메일 발송")
    @PostMapping("/auth-email")
    public ResponseEntity<ResponseMessageDto> sendAuthEmail(@Valid @RequestBody EmailDto emailDto) throws MessagingException {

        // 인증 메일 발송 - Async 메서드
        // 인증 메일 발송 성공 시 2000 상태코드 반환
        userService.sendAuthEmail(emailDto.getEmail());
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "인증 번호 체크")
    @PostMapping("/")
    public ResponseEntity<ResponseMessageDto> checkAuthNumber(@Valid @RequestBody AuthEmailCheckDto authEmailCheckDto) {

        // 인증 번호 체크
        // 인증 실패 시 4014 상태코드 반환, 성공 시 2000 상태코드 반환
        boolean isAuthEmailCheck = userService.checkAuthNumber(authEmailCheckDto.getEmail(), authEmailCheckDto.getAuthNumber());
        if (!isAuthEmailCheck) {

            StatusCode statusCode = StatusCode.AUTH_EMAIL_CHECK_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            StatusCode statusCode = StatusCode.SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "이메일 찾기")
    @GetMapping("/email")
    public ResponseEntity<ResponseMessageDto> findEmailByPhoneNumber(@Valid @RequestBody PhoneNumberDto phoneNumberDto) {

        // 이메일 찾기
        // 이메일 찾기 성공 시 masking 된 이메일 반환, 실패 시 null 반환
        String maskingEmail = userService.findEmailByPhoneNumber(phoneNumberDto.getPhoneNumber());
        if (maskingEmail == null) {

            StatusCode statusCode = StatusCode.FIND_EAMIL_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            StatusCode statusCode = StatusCode.SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            responseMessageDto.addData("email", maskingEmail);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "회원 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ResponseMessageDto> getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 회원 정보 조회
        // 조회 성공 시 UserInfoDto 객체 반환, 실패 시 null 반환
        UserInfoDto userInfo = userService.getUserInfo(email);
        if (userInfo == null) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            StatusCode statusCode = StatusCode.SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            responseMessageDto.addData("user", userInfo);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "이메일 변경")
    @PutMapping("/me/email")
    public ResponseEntity<ResponseMessageDto> updateEmail(@Valid @RequestBody EmailDto updateEmailDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String updateEmail = updateEmailDto.getEmail();

        // 변경 후 이메일 중복 검증
        // 이미 존재하는 이메일이라면 true, 존재하지 않는 이메일이라면 false
        boolean isEmailDuplicate = userService.isEmailExists(updateEmail);
        if (isEmailDuplicate) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 이메일 변경
        userService.updateEmail(email, updateEmail);
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateEmail", updateEmail);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "이름 변경")
    @PutMapping("/me/name")
    public ResponseEntity<ResponseMessageDto> updateName(@Valid @RequestBody UsernameDto usernameDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 이름 변경
        userService.updateName(email, usernameDto.getUsername());
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateName", usernameDto.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "닉네임 변경")
    @PutMapping("/me/nickname")
    public ResponseEntity<ResponseMessageDto> updateNickname(@Valid @RequestBody NicknameDto nicknameDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        String updateNickname = nicknameDto.getNickname();

        // 닉네임 중복 검증
        // 이미 존재하는 닉네임일 경우 true, 존재 하지 않는 닉네임인 경우 false
        boolean isNicknameExists = userService.isNicknameExists(updateNickname);
        if (isNicknameExists) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 사용자 닉네임 변경
        userService.updateNickname(email, updateNickname);
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateNickname", updateNickname);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "소개글 변경")
    @PutMapping("/me/user-desc")
    public ResponseEntity<ResponseMessageDto> updateUserDesc(@Valid @RequestBody UserDescDto userDescDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 소개글 변경
        userService.updateUserDesc(email, userDescDto.getUserDesc());
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateUserDesc", userDescDto.getUserDesc());
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "비밀번호 찾기")
    @PostMapping("/password")
    public ResponseEntity<ResponseMessageDto> findPassword(@Valid @RequestBody PhoneNumberAndEmailDto phoneNumberAndEmailDto) throws MessagingException {

        String email = phoneNumberAndEmailDto.getEmail();
        String phoneNumber = phoneNumberAndEmailDto.getPhoneNumber();

        // 사용자가 입력한 이메일, 연락처 매칭
        // 이메일이 존재하고 이메일과 연락처가 매칭되면 true, 이메일이 존재하지 않거나 이메일과 연락처가 매칭되지 않으면 false
        boolean isMatched = userService.checkMatchingEmailAndPhoneNumber(email, phoneNumber);
        if (!isMatched) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 임시 비밀번호 발급 - Async 메서드
        userService.sendTempPasswordEmail(email);
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "비밀번호 확인 및 변경")
    @PutMapping("/me/password")
    public ResponseEntity<ResponseMessageDto> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 수정 전 비밀번호, 수정 후 비밀번호 비교
        // 수정 전 비밀번호와 수정 후 비밀번호가 같을 경우 4017 상태코드 반환
        String oldPassword = updatePasswordDto.getOldPassword();
        String newPassword = updatePasswordDto.getNewPassword();
        if (oldPassword.equals(newPassword)) {

            StatusCode statusCode = StatusCode.USER_INFO_UPDATE_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 비밀번호 확인
        // 입력한 비밀번호가 현재 비밀번호와 일치하면 true, 일치하지 않으면 false
        boolean isCheckPassword = userService.checkPassword(email, oldPassword);
        if (!isCheckPassword) {

            StatusCode statusCode = StatusCode.USER_INFO_UPDATE_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 비밀번호 수정
        userService.updatePassword(email, newPassword);
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "회원 구매 입찰 내역 조회")
    @GetMapping("/me/buy")
    public ResponseEntity<ResponseMessageDto> getBuyBidList(@RequestParam(value = "per_page", defaultValue = "10") int perPage,
                                                            @RequestParam(value = "cursor", defaultValue = "1") int cursor,
                                                            @RequestParam(value = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                            @RequestParam(value = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 정보 조회
        UserInfoDto userInfo = userService.getUserInfo(email);
        Long userNo = userInfo.getUserNo();

        // 페이징 및 구매 입찰 목록 조회
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);
        Page<Bid> pagingBuyBid = userService.getPagedBuyBidByUserNo(userNo, startDate, endDate, pageRequest);

        // 페이징한 구매 입찰 목록으로 구매 입찰 정보 리스트를 작성
        List<UserBidDto> userBidDtoList = userService.setUserBidInfo(pagingBuyBid);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("buyBidList", userBidDtoList);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "회원 판매 입찰 내역 조회")
    @GetMapping("/me/sell")
    public ResponseEntity<ResponseMessageDto> getSellBidList(@RequestParam(value = "per_page", defaultValue = "10") int perPage,
                                                             @RequestParam(value = "cursor", defaultValue = "1") int cursor,
                                                             @RequestParam(value = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                             @RequestParam(value = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 정보 조회
        UserInfoDto userInfo = userService.getUserInfo(email);
        Long userNo = userInfo.getUserNo();

        // 페이징 및 판매 입찰 목록 조회
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);
        Page<Bid> pagingSellBid = userService.getPagedSellBidByUserNo(userNo, startDate, endDate, pageRequest);

        // 페이징한 판매 입찰 목록으로 판매 입찰 정보 리스트를 작성
        List<UserBidDto> userBidDtoList = userService.setUserBidInfo(pagingSellBid);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("sellBidList", userBidDtoList);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation("회원 입찰 정보 상세 조회")
    @GetMapping("/me/bid/{bid_no}")
    public ResponseEntity<ResponseMessageDto> getBidDetail(@PathVariable("bid_no") Long bidNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 정보 조회
        UserInfoDto userInfo = userService.getUserInfo(email);
        Long userNo = userInfo.getUserNo();

        // 입찰 상세 정보 조회
        UserBidDetailDto bidDetailInfo = userService.getBidDetailInfo(userNo, bidNo);
        if (bidDetailInfo == null) {

            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("BidDetail", bidDetailInfo);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation("회원 입찰 정보 삭제")
    @DeleteMapping("/me/bid/{bid_no}")
    public ResponseEntity<ResponseMessageDto> deleteBid(@PathVariable("bid_no") Long bidNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 정보 조회
        UserInfoDto userInfo = userService.getUserInfo(email);
        Long userNo = userInfo.getUserNo();

        // 사용자가 입찰한 내역인지 검증 후 삭제
        // 성공 시 true, 실패 시 false
        boolean isDeleted = userService.deleteBid(userNo, bidNo);
        if (!isDeleted) {

            StatusCode statusCode = StatusCode.USER_INFO_UPDATE_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }


    @ApiOperation(value = "포인트 조회")
    @GetMapping("/me/point")
    public ResponseEntity<ResponseMessageDto> getPoints() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        UserInfoDto userInfoDto = new UserInfoDto(optionalUser.get());
        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("point", userInfoDto.getPoint());
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "회원 탈퇴") //비활성화
    @DeleteMapping("/me")
    public ResponseEntity<ResponseMessageDto> deActivateUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        userService.DeActivateUser(email);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

    }

    //=========================================== 배송지 ======================================================================================


    @ApiOperation(value = "회원 배송지 추가")
    @PostMapping("/me/address")
    public ResponseEntity<ResponseMessageDto> addAddress(@RequestBody AddressDto addressDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        addressService.addAddress(optionalUser.get(), addressDto);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "회원 배송지 조회")
    @GetMapping("/me/address")
    public ResponseEntity<ResponseMessageDto> getAddressList(@RequestParam(value = "per_page", defaultValue = "10") int perPage,
                                                             @RequestParam(value = "cursor", defaultValue = "1")int cursor) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        Sort sort = Sort.by(Sort.Direction.DESC,"addressNo");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);

        Page<Address> pagingAddress = addressService.getPagedAddressesByUser(optionalUser.get(),pageRequest);

        List<AddressDto> addressDtoList = addressService.setAddressDto(pagingAddress);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("addressList", addressDtoList);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

    }

    @ApiOperation(value = "회원 배송지 수정")
    @PatchMapping("/me/address/{address_no}")
    public ResponseEntity<ResponseMessageDto> updateAddress(@RequestBody AddressDto newaddressDto, @PathVariable("address_no") Long addressNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
        addressService.updateAddress(addressNo, newaddressDto);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "회원 기본배송지 설정")
    @PatchMapping("/me/address/{address_no}/default")
    public ResponseEntity<ResponseMessageDto> setDefaultAddress(@PathVariable("address_no") Long addressNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        addressService.setDefaultAddress(addressNo);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

    }

    @ApiOperation(value = "배송지 삭제")
    @DeleteMapping("/me/address")
    public ResponseEntity<ResponseMessageDto> deleteAddress(@RequestParam("address_no") Long addressNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        addressService.deleteAddress(addressNo);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }


    //=========================================== 위시리스트 ======================================================================================

    @ApiOperation(value = "관심 상품 조회")
    @GetMapping("/me/wishlist")
    public ResponseEntity<ResponseMessageDto> getWishLists (@RequestParam(value = "per_page", defaultValue = "10") int perPage,
                                                            @RequestParam(value = "cursor", defaultValue = "1")int cursor) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }


        Sort sort = Sort.by(Sort.Direction.DESC,"productSizeNo");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);
        // wishlist에 잇는 userno 추출 하고, 거기 해당하는 productsize 다시 리스트 추가해야함.
        List<Wishlist> myWishList = wishlistService.getUserWishList(optionalUser.get());
        List<ProductSize> myWishProductSizes = productSizeService.getMyWishListProductSizes(myWishList);

        Page<ProductSize> pagingProductSize = productSizeService.getPagedProductSizes(myWishProductSizes, pageRequest);
        List<ProductSizeDto> productSizeDtoList = productSizeService.setProductSizeDtoList(pagingProductSize);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("productSizeDtoList", productSizeDtoList);

        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "관심 상품 등록/해제")
    @PostMapping("/me/wishlist")
    public ResponseEntity<ResponseMessageDto> addOrDeleteWishList(@RequestBody ProductSizeDto productSizeDto){ //토글//product Size No?받아와야함.
        //if userno가 wishlist에 잇으면 해제. 없으면 등록.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();


        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        ProductSize productSize = productSizeService.getProductSizeByNo(productSizeDto.getProductSizeNo());
        boolean isWishlistRegistered = wishlistService.updateWishlist(optionalUser.get(), productSize);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("isWishlistRegistered" , isWishlistRegistered); // 상품정보
        responseMessageDto.addData("productSizeNo" , productSizeDto.getProductSizeNo());
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }


}