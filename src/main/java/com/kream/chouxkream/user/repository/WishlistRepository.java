package com.kream.chouxkream.user.repository;

import com.kream.chouxkream.productsize.ProductSize;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.Wishlist;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    @Query("SELECT w FROM Wishlist w WHERE w.user.userNo = :userNo ")
    List<Wishlist> findAllByUserNo(@Param("userNo") Long userNo);

    boolean existsByUserAndProductSize(User user, ProductSize productSize);

}
