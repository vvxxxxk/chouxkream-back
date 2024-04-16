package com.kream.chouxkream.user.repository;

import com.kream.chouxkream.user.model.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<Address> findByUserUserNo(Long userNo, Pageable pageable);

    Optional<Address> findByUserUserNoAndDefaultAddressIsTrue(Long userNo);
}