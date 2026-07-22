package com.jk1.service;

import com.jk1.entity.Cart;
import java.util.List;
import java.util.Optional;

public interface CartService {
    Cart getCartForUser(String email);
    void addProductToCart(String email, Long productId, int quantity, String selectedSize, String selectedColor);
    void updateCartItemQuantity(String email, Long cartItemId, int quantity);
    void removeCartItem(String email, Long cartItemId);
    void clearCart(String email);
}
