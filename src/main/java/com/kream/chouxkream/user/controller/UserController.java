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
    private final WishlistService wishlistService;
    private final AddressService addressService;
    private final UserService userService;
    private final ProductService productService;
    private final ProductSizeService productSizeService;

    @ApiOperation(value = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<ResponseMessageDto> join(@Valid @RequestBody UserJoinDto userJoinDto) {

        // 이메일 인증 여부 체크
        if (!userJoinDto.isEmailAuth()) {

            StatusCode statusCode = StatusCode.MISSING_REQUIRED_FIELD;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 이메일 중복 체크
        boolean isEmailDuplicate = userService.isEmailExists(userJoinDto.getEmail());
        if (isEmailDuplicate) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 회원가입
        userService.join(userJoinDto);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "인증 메일 발송")
    @PostMapping("/auth-email")
    public ResponseEntity<ResponseMessageDto> sendAuthEmail(@Valid @RequestBody EmailDto emailDto) throws MessagingException {

        // Async 메서드
        userService.sendAuthEmail(emailDto.getEmail());

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "인증 번호 체크")
    @PostMapping("/")
    public ResponseEntity<ResponseMessageDto> checkAuthNumber(@Valid @RequestBody AuthEmailCheckDto authEmailCheckDto) {

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

        Optional<User> optionalUser = userService.findByPhoneNumber(phoneNumberDto.getPhoneNumber());

        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_EAMIL_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            StatusCode statusCode = StatusCode.SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            String maskingEmail = emailMasking(optionalUser.get().getEmail());
            responseMessageDto.addData("email", maskingEmail);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    private String emailMasking(String email) {

        int pos = email.indexOf('@');
        String id = email.substring(0, pos);

        // 아이디 앞 2글자를 제외한 나머지를 *로 마스킹
        return id.substring(0, Math.min(id.length(), 2)) +
                email.substring(2, pos).replaceAll(".", "*") +  // 정규 표현식에서 .은 임의의 문자 하나를 의미
                email.substring(pos);
    }

    @ApiOperation(value = "회원 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ResponseMessageDto> getUserInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        } else {

            UserInfoDto userInfoDto = new UserInfoDto(optionalUser.get());

            StatusCode statusCode = StatusCode.SUCCESS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            responseMessageDto.addData("user", userInfoDto);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "이메일 변경")
    @PutMapping("/me/email")
    public ResponseEntity<ResponseMessageDto> updateEmail(@Valid @RequestBody EmailDto updateEmailDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String updateEmail = updateEmailDto.getEmail();

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 변경 후 이메일 중복 검증
        boolean isEmailDuplicate = userService.isEmailExists(updateEmail);
        if (isEmailDuplicate) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

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

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        String updateName = usernameDto.getUsername();
        userService.updateName(email, updateName);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateName", updateName);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "닉네임 변경")
    @PutMapping("/me/nickname")
    public ResponseEntity<ResponseMessageDto> updateNickname(@Valid @RequestBody NicknameDto nicknameDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        String updateNickname = nicknameDto.getNickname();

        // 닉네임 중복 검증
        boolean isNicknameExists = userService.isNicknameExists(updateNickname);
        if (isNicknameExists) {

            StatusCode statusCode = StatusCode.RESOURCE_ALREADY_EXISTS;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

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

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        String updateUserDesc = userDescDto.getUserDesc();
        userService.updateUserDesc(email, updateUserDesc);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("updateUserDesc", updateUserDesc);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation(value = "비밀번호 찾기")
    @PostMapping("/password")
    public ResponseEntity<ResponseMessageDto> findPassword(@Valid @RequestBody PhoneNumberAndEmailDto phoneNumberAndEmailDto) throws MessagingException {

        String email = phoneNumberAndEmailDto.getEmail();
        String phoneNumber = phoneNumberAndEmailDto.getPhoneNumber();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
        // 사용자 정보와 입력한 연락처 비교
        if (!optionalUser.get().getPhoneNumber().equals(phoneNumber)) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 임시 비밀번호 발급
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

        // 사용자 검증
        boolean isExistEmail = userService.isEmailExists(email);
        if (!isExistEmail) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 수정 전 비밀번호, 수정 후 비밀번호 비교
        String oldPassword = updatePasswordDto.getOldPassword();
        String newPassword = updatePasswordDto.getNewPassword();
        if (oldPassword.equals(newPassword)) {

            StatusCode statusCode = StatusCode.USER_INFO_UPDATE_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 입력한 비밀번호 검증
        boolean isCheckPassword = userService.isPasswordCheck(email, oldPassword);
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

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        Long userNo = optionalUser.get().getUserNo();

        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);

        Page<Bid> pagingBuyBid = userService.getPagedBuyBidByUserNo(userNo, startDate, endDate, pageRequest);

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

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        Long userNo = optionalUser.get().getUserNo();
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);
        Page<Bid> pagingSellBid = userService.getPagedSellBidByUserNo(userNo, startDate, endDate, pageRequest);

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

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 결제 정보 조회
        Optional<Bid> optionalBid = userService.getBuyBidByBidNo(bidNo);
        if (optionalBid.isEmpty()) {

            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        Bid bid = optionalBid.get();
        UserBidDetailDto userBidDetailDto = userService.setUserBidDetailInfo(bid);

        StatusCode statusCode = StatusCode.SUCCESS;
        ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
        responseMessageDto.addData("buyBidDetail", userBidDetailDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
    }

    @ApiOperation("회원 입찰 정보 삭제")
    @DeleteMapping("/me/bid/{bid_no}")
    public ResponseEntity<ResponseMessageDto> deleteBid(@PathVariable("bid_no") Long bidNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 사용자 조회
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {

            StatusCode statusCode = StatusCode.FIND_USER_FAILED;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

        // 결제 정보 조회
        Optional<Bid> optionalBid = userService.getBuyBidByBidNo(bidNo);
        if (optionalBid.isEmpty()) {

            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = new ResponseMessageDto(statusCode.getCode(), statusCode.getMessage(), null);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
        Bid bid = optionalBid.get();

        userService.deleteBid(bid);


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
        List<ProductSizeDto> productSizeDtoList = productSizeService.setProductSizeDto(pagingProductSize);

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