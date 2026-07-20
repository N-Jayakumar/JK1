package com.jk1.controller;

import com.jk1.entity.Wishlist;
import com.jk1.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Handles all wishlist-related HTTP requests for JKØ.
 *
 * <p>Every action is guarded by a null-check on {@link Principal}; unauthenticated
 * requests are redirected to the login page before any service call is made.</p>
 *
 * <p>The GET /wishlist handler delegates to {@link WishlistService#getWishlistForUser(String)}
 * which creates a fresh, empty wishlist on first access — so the template always
 * receives a non-null {@code Wishlist} object and can render the empty-state view
 * without throwing an exception.</p>
 */
@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // ─── View ─────────────────────────────────────────────────────────────────

    /**
     * Renders the wishlist page.
     *
     * <p>If the user has no wishlist yet, {@link WishlistService#getWishlistForUser}
     * creates an empty one. The template handles the empty-state display.</p>
     *
     * @return Thymeleaf template path: templates/wishlist/view.html
     */
    @GetMapping
    public String viewWishlist(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            Wishlist wishlist = wishlistService.getWishlistForUser(principal.getName());
            model.addAttribute("wishlist", wishlist);
        } catch (Exception e) {
            model.addAttribute("wishlist", null);
            model.addAttribute("wishlistError", "Unable to load wishlist. Please try again.");
        }
        return "wishlist/view";
    }

    // ─── Mutations ─────────────────────────────────────────────────────────────

    /**
     * Adds a product to the wishlist.
     */
    @PostMapping("/add/{productId}")
    public String addToWishlist(@PathVariable Long productId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            wishlistService.addProductToWishlist(principal.getName(), productId);
            redirectAttributes.addFlashAttribute("wishlistSuccess", "Item added to wishlist!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("wishlistError", e.getMessage());
        }
        return "redirect:/wishlist";
    }

    /**
     * Removes a product from the wishlist.
     */
    @PostMapping("/remove/{productId}")
    public String removeFromWishlist(@PathVariable Long productId,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            wishlistService.removeProductFromWishlist(principal.getName(), productId);
            redirectAttributes.addFlashAttribute("wishlistSuccess", "Item removed from wishlist.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("wishlistError", e.getMessage());
        }
        return "redirect:/wishlist";
    }

    /**
     * Moves a product from the wishlist to the cart.
     */
    @PostMapping("/move-to-cart/{productId}")
    public String moveToCart(@PathVariable Long productId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            wishlistService.moveProductToCart(principal.getName(), productId);
            redirectAttributes.addFlashAttribute("cartSuccess", "Item moved to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("wishlistError", e.getMessage());
            return "redirect:/wishlist";
        }
        return "redirect:/cart";
    }
}
