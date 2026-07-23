package com.jk1.service.impl;

import com.jk1.entity.Cart;
import com.jk1.entity.CartItem;
import com.jk1.entity.Product;
import com.jk1.entity.User;
import com.jk1.repository.CartItemRepository;
import com.jk1.repository.CartRepository;
import com.jk1.repository.ProductRepository;
import com.jk1.repository.UserRepository;
import com.jk1.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Cart getCartForUser(String email) {
        Cart cart = cartRepository.findByUserEmail(email).orElseGet(() -> createNewCart(email));
        if (cart.getItems() == null) {
            cart.setItems(new java.util.ArrayList<>());
        }
        return cart;
    }

    private Cart createNewCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new java.util.ArrayList<>());
        cart.setTotalAmount(BigDecimal.ZERO);
        return cartRepository.save(cart);
    }

    @Override
    public void addProductToCart(String email, Long productId, int quantity, String selectedSize, String selectedColor) {
        Cart cart = getCartForUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() == null || product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId) &&
                        java.util.Objects.equals(item.getSelectedSize(), selectedSize) &&
                        java.util.Objects.equals(item.getSelectedColor(), selectedColor))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (product.getQuantity() < (item.getQuantity() + quantity)) {
                throw new RuntimeException("Not enough stock available for additional quantity");
            }
            item.setQuantity(item.getQuantity() + quantity);
            
            // Update price in case it changed
            BigDecimal price = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
            item.setPrice(price != null ? price : BigDecimal.ZERO);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setSelectedSize(selectedSize);
            newItem.setSelectedColor(selectedColor);
            
            BigDecimal price = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
            newItem.setPrice(price != null ? price : BigDecimal.ZERO);
            
            cart.getItems().add(newItem);
        }

        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public void updateCartItemQuantity(String email, Long cartItemId, int quantity) {
        Cart cart = getCartForUser(email);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (cartItem.getProduct().getQuantity() == null || cartItem.getProduct().getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    public void removeCartItem(String email, Long cartItemId) {
        Cart cart = getCartForUser(email);
        if (cart.getItems() != null) {
            cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
            recalculateCartTotal(cart);
            cartRepository.save(cart);
        }
    }

    @Override
    public void clearCart(String email) {
        Cart cart = getCartForUser(email);
        if (cart.getItems() != null) {
            cart.getItems().clear();
        }
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private void recalculateCartTotal(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            cart.setTotalAmount(BigDecimal.ZERO);
            return;
        }
        
        BigDecimal total = cart.getItems().stream()
                .map(item -> {
                    BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
    }
}
