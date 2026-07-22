package com.jk1.service.impl;

import com.jk1.entity.Product;
import com.jk1.entity.User;
import com.jk1.entity.Wishlist;
import com.jk1.repository.ProductRepository;
import com.jk1.repository.UserRepository;
import com.jk1.repository.WishlistRepository;
import com.jk1.service.CartService;
import com.jk1.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Override
    public Wishlist getWishlistForUser(String email) {
        Wishlist wishlist = wishlistRepository.findByUserEmail(email).orElseGet(() -> createNewWishlist(email));
        if (wishlist.getProducts() == null) {
            wishlist.setProducts(new java.util.HashSet<>());
        }
        return wishlist;
    }

    private Wishlist createNewWishlist(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProducts(new java.util.HashSet<>());
        return wishlistRepository.save(wishlist);
    }

    @Override
    public void addProductToWishlist(String email, Long productId) {
        Wishlist wishlist = getWishlistForUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        wishlist.getProducts().add(product);
        wishlistRepository.save(wishlist);
    }

    @Override
    public void removeProductFromWishlist(String email, Long productId) {
        Wishlist wishlist = getWishlistForUser(email);
        wishlist.getProducts().removeIf(p -> p.getId().equals(productId));
        wishlistRepository.save(wishlist);
    }

    @Override
    public void moveProductToCart(String email, Long productId) {
        // Remove from wishlist
        removeProductFromWishlist(email, productId);
        // Add to cart with quantity 1
        cartService.addProductToCart(email, productId, 1, null, null);
    }
}
