package com.kream.chouxkream.user.service;

import com.kream.chouxkream.user.ResourceNotFoundException;
import com.kream.chouxkream.user.model.dto.AddressDto;
import com.kream.chouxkream.user.model.entity.Address;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public void addAddress(User user, AddressDto addressDto) {
        Address address = new Address();
        address.setUser(user);
        address.setReceiverName(addressDto.getReceiverName());
        address.setReceiverPhone(addressDto.getReceiverPhone());
        address.setZipcode(addressDto.getZipcode());
        address.setAddress(addressDto.getAddress());
        address.setDefaultAddress(addressDto.isDefaultAddress());
        if (addressDto.getDetailAddress() == null || addressDto.getDetailAddress().isBlank()) {
            address.unFilleddetailAddress();
            addressRepository.save(address);
        } else {
            address.setDetailAddress(addressDto.getDetailAddress());
            addressRepository.save(address);
        }
    }

    public void updateAddress(Long addressNo, AddressDto newaddressDto) throws ResourceNotFoundException {
        Address address = addressRepository.findById(addressNo)
                .orElseThrow(()-> new ResourceNotFoundException("not found address."));
        address.setReceiverName(newaddressDto.getReceiverName());
        address.setReceiverPhone(newaddressDto.getReceiverPhone());
        address.setZipcode(newaddressDto.getZipcode());
        address.setAddress(newaddressDto.getAddress());
        address.setDefaultAddress(newaddressDto.isDefaultAddress());
        address.setDetailAddress(newaddressDto.getDetailAddress());
        addressRepository.save(address);
    }

    public void setDefaultAddress(Long addressNo) throws ResourceNotFoundException {
        Address address = addressRepository.findById(addressNo)
                .orElseThrow(()-> new ResourceNotFoundException("not found address."));
        if (!address.isDefaultAddress()) {
            address.setDefaultAddress(true);
            addressRepository.save(address);
        }
    }

    public Page<Address> getPagedAddressesByUser(User user, PageRequest pageRequest) {
        return addressRepository.findByUserUserNo(user.getUserNo(), pageRequest);
    }

    public List<AddressDto> setAddressDto(Page<Address> pagingAddress) {
        List<AddressDto> userAddressDtoList = new ArrayList<>();
        for (Address address : pagingAddress) {
            AddressDto addressDto = new AddressDto();
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

    public void deleteAddress(Long addressNo) throws ResourceNotFoundException{
        Address address = addressRepository.findById(addressNo)
                .orElseThrow(()->new ResourceNotFoundException("not found address."));
        addressRepository.delete(address);
    }

}
