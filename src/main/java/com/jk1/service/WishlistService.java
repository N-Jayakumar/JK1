package com.jk1.service;

import com.jk1.entity.Wishlist;
import java.util.List;
import java.util.Optional;

public interface WishlistService {
    Wishlist getWishlistForUser(String email);
    void addProductToWishlist(String email, Long productId);
    void removeProductFromWishlist(String email, Long productId);
    void moveProductToCart(String email, Long productId);
}
