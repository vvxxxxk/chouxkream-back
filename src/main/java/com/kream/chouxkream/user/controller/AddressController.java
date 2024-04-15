package com.kream.chouxkream.user.controller;

import com.kream.chouxkream.common.model.dto.ResponseMessageDto;
import com.kream.chouxkream.common.model.dto.StatusCode;
import com.kream.chouxkream.productsize.ProductSize;
import com.kream.chouxkream.user.ResourceNotFoundException;
import com.kream.chouxkream.user.model.dto.AddressDto;
import com.kream.chouxkream.user.model.entity.Address;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.service.AddressService;
import com.kream.chouxkream.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/address")
public class AddressController {
    private final AddressService addressService;
    private final UserService userService;

    @ApiOperation(value = "회원 배송지 추가")
    @PostMapping
    public ResponseEntity<ResponseMessageDto> addAddress(@RequestBody AddressDto addressDto) {
        //        String email = getSiteUserEmail();
        String email = "ee121111@test.com";
        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("not found user"));

            addressService.addAddress(user, addressDto);

            StatusCode statusCode = StatusCode.FIND_USER_SUCCESS;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

        } catch (ResourceNotFoundException ex) {
            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "회원 배송지 조회")
    @GetMapping
    public ResponseEntity<ResponseMessageDto> getAddressList(@RequestParam(value = "per_page", defaultValue = "10") int perPage,
                                                             @RequestParam(value = "cursor", defaultValue = "1")int cursor) {
        //        String email = getSiteUserEmail();
        String email = "ee121111@test.com";
        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("not found user"));

            Sort sort = Sort.by(Sort.Direction.DESC,"addressNo");
            PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);

            Page<Address> pagingAddress = addressService.getPagedAddressesByUser(user,pageRequest);

            List<AddressDto> addressDtoList = addressService.setAddressDto(pagingAddress);

            StatusCode statusCode = StatusCode.FIND_USER_SUCCESS;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            responseMessageDto.addData("addressList", addressDtoList);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

        } catch (ResourceNotFoundException ex) {
            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }

    }

    @ApiOperation(value = "회원 배송지 수정")
    @PatchMapping("/{address_no}")
    public ResponseEntity<ResponseMessageDto> updateAddress(@RequestBody AddressDto newaddressDto, @PathVariable("address_no") Long addressNo) {
        //        String email = getSiteUserEmail();
        String email = "ee121111@test.com";
        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("not found user"));

            addressService.updateAddress(addressNo, newaddressDto);

            StatusCode statusCode = StatusCode.FIND_USER_SUCCESS;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

        } catch (ResourceNotFoundException ex) {
            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
        }
    }

    @ApiOperation(value = "회원 기본배송지 설정")
    @PatchMapping("/{address_no}/default")
    public ResponseEntity<ResponseMessageDto> setDefaultAddress(@PathVariable("address_no") Long addressNo) {
        //        String email = getSiteUserEmail();
        String email = "ee121111@test.com";
        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("not found user"));

            addressService.setDefaultAddress(addressNo);

            StatusCode statusCode = StatusCode.FIND_USER_SUCCESS;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);

        } catch (ResourceNotFoundException ex) {
            StatusCode statusCode = StatusCode.PRODUCT_NOT_FOUND;
            ResponseMessageDto responseMessageDto = setResponseMessageDto(statusCode);
            return ResponseEntity.status(HttpStatus.OK).body(responseMessageDto);
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
