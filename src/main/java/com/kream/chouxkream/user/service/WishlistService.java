package com.kream.chouxkream.user.service;

import com.kream.chouxkream.productsize.model.entity.ProductSize;
import com.kream.chouxkream.user.model.entity.User;
import com.kream.chouxkream.user.model.entity.Wishlist;
import com.kream.chouxkream.user.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishListRepository;

    public List<Wishlist> getUserWishList (User user) {
        return this.wishListRepository.findAllByUserNo(user.getUserNo());
    }

    public void updateWishlist (User user, ProductSize productSize) {
        if (!wishListRepository.existsByUserAndProductSize(user, productSize)){ //없으면 추가
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            this.wishListRepository.save(wishlist);

            productSize.setWishlist(wishlist);
            this.wishListRepository.save(wishlist);
        } else { //있는 경우
            Wishlist wishlist = productSize.getWishlist();
            this.wishListRepository.delete(wishlist);
        }
    }




}
