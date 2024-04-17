package com.kream.chouxkream.user.service;

import com.kream.chouxkream.user.model.dto.AddressDto;
import com.kream.chouxkream.user.model.entity.Address;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    @Transactional
    public void addAddress(User user, AddressDto addressDto) {
        Address address = new Address();
        address.setUser(user);
        address.setReceiverName(addressDto.getReceiverName());
        address.setReceiverPhone(addressDto.getReceiverPhone());
        address.setZipcode(addressDto.getZipcode());
        address.setAddress(addressDto.getAddress());
        if (addressDto.isDefaultAddress()) {
            unSetDefaultAddress(user.getUserNo());
        }
        address.setDefaultAddress(addressDto.isDefaultAddress());
        address.setDetailAddress(address.getDetailAddress());
        addressRepository.save(address);
    }

    @Transactional
    public void updateAddress(Long addressNo, AddressDto newaddressDto){
        Address address = addressRepository.findById(addressNo).get();
        address.setReceiverName(newaddressDto.getReceiverName());
        address.setReceiverPhone(newaddressDto.getReceiverPhone());
        address.setZipcode(newaddressDto.getZipcode());
        address.setAddress(newaddressDto.getAddress());
        if (newaddressDto.isDefaultAddress()) {
            unSetDefaultAddress(address.getUser().getUserNo());
        }
        address.setDefaultAddress(newaddressDto.isDefaultAddress());
        address.setDetailAddress(newaddressDto.getDetailAddress());
        addressRepository.save(address);
    }

    @Transactional
    public void setDefaultAddress(Long addressNo) {
        Address address = addressRepository.findById(addressNo).get();
        if (!address.isDefaultAddress()) {
            unSetDefaultAddress(address.getUser().getUserNo());
            address.setDefaultAddress(true);
            addressRepository.save(address);
        }
    }

    @Transactional
    public Page<Address> getPagedAddressesByUser(User user, PageRequest pageRequest) {
        return addressRepository.findByUserUserNo(user.getUserNo(), pageRequest);
    }

    @Transactional
    public List<AddressDto> setAddressDto(Page<Address> pagingAddress) {
        List<AddressDto> userAddressDtoList = new ArrayList<>();
        for (Address address : pagingAddress) {
            AddressDto addressDto = new AddressDto();
            addressDto.setAddressNo(address.getAddressNo());
            addressDto.setReceiverName(address.getReceiverName());
            addressDto.setReceiverPhone(address.getReceiverPhone());
            addressDto.setZipcode(address.getZipcode());
            addressDto.setAddress(address.getAddress());
            addressDto.setDetailAddress(address.getDetailAddress());
            addressDto.setDefaultAddress(address.isDefaultAddress());

            userAddressDtoList.add(addressDto);
        }
        return userAddressDtoList;
    }

    @Transactional
    public void deleteAddress(Long addressNo){
        Address address = addressRepository.findById(addressNo).get();
        addressRepository.delete(address);
    }

    public Address getMyDefaultAddress(Long userNo) { //없으면? 냅두고 잇으면 바꾸는거.
        return this.addressRepository.findByUserUserNoAndDefaultAddressIsTrue(userNo)
                .orElse(null);
    }

//    public void unSetDefaultAddress(Long userNo) {
//        Optional<Address> defaultAddress = this.addressRepository.findByUserUserNoAndDefaultAddressIsTrue(userNo);
//        defaultAddress.ifPresent( myDefaultAddress -> {
//            myDefaultAddress.setDefaultAddress(false);
//            this.addressRepository.save(myDefaultAddress);
//        });
////        if (myDefaultAddress.isPresent()){
////            myDefaultAddress.get().setDefaultAddress(false);
////            this.addressRepository.save(myDefaultAddress.get());
////        }
//    }

    @Transactional
    public void unSetDefaultAddress(Long userNo) {
        Optional<Address> myDefaultAddressOptional = this.addressRepository.findByUserUserNoAndDefaultAddressIsTrue(userNo);
        myDefaultAddressOptional.ifPresent(myDefaultAddress -> {
            myDefaultAddress.setDefaultAddress(false);
            this.addressRepository.save(myDefaultAddress);
        });
    }

}
