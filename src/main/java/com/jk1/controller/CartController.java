package com.jk1.controller;

import com.jk1.entity.Cart;
import com.jk1.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Handles all cart-related HTTP requests for JKØ.
 *
 * <p>Every action is guarded by a null-check on {@link Principal}; unauthenticated
 * requests are redirected to the login page before any service call is made,
 * preventing NullPointerException chains in the service layer.</p>
 *
 * <p>The GET /cart handler delegates to {@link CartService#getCartForUser(String)}
 * which creates a fresh, empty cart on first access — so the template always
 * receives a non-null {@code Cart} object and can render the empty-state view
 * without throwing an exception.</p>
 */
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // ─── View ─────────────────────────────────────────────────────────────────

    /**
     * Renders the shopping cart page.
     *
     * <p>If the user has no cart yet, {@link CartService#getCartForUser} creates
     * an empty one. The template handles the empty-state display.</p>
     *
     * @return Thymeleaf template path: templates/cart/view.html
     */
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            Cart cart = cartService.getCartForUser(principal.getName());
            
            int totalItems = 0;
            java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
            java.math.BigDecimal discount = java.math.BigDecimal.ZERO;
            
            if (cart != null && cart.getItems() != null) {
                for (com.jk1.entity.CartItem item : cart.getItems()) {
                    totalItems += item.getQuantity();
                    java.math.BigDecimal originalPrice = item.getProduct().getPrice();
                    if (originalPrice == null) originalPrice = java.math.BigDecimal.ZERO;
                    
                    java.math.BigDecimal itemOriginalTotal = originalPrice.multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
                    subtotal = subtotal.add(itemOriginalTotal);
                    
                    java.math.BigDecimal finalPrice = item.getPrice() != null ? item.getPrice() : java.math.BigDecimal.ZERO;
                    java.math.BigDecimal itemFinalTotal = finalPrice.multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
                    discount = discount.add(itemOriginalTotal.subtract(itemFinalTotal));
                }
            }
            
            model.addAttribute("cart", cart);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("cartSubtotal", subtotal);
            model.addAttribute("cartDiscount", discount);
            
        } catch (Exception e) {
            model.addAttribute("cart", null);
            model.addAttribute("cartError", "Unable to load cart. Please try again.");
        }
        return "cart/view";
    }

    // ─── Mutations ─────────────────────────────────────────────────────────────

    /**
     * Adds a product to the cart.
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            cartService.addProductToCart(principal.getName(), productId, quantity);
            redirectAttributes.addFlashAttribute("cartSuccess", "Item added to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * Updates the quantity of an existing cart item.
     */
    @PostMapping("/update/{itemId}")
    public String updateCartItem(@PathVariable Long itemId,
                                 @RequestParam int quantity,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            if (quantity <= 0) {
                cartService.removeCartItem(principal.getName(), itemId);
            } else {
                cartService.updateCartItemQuantity(principal.getName(), itemId, quantity);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * Removes a single item from the cart.
     */
    @PostMapping("/remove/{itemId}")
    public String removeCartItem(@PathVariable Long itemId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            cartService.removeCartItem(principal.getName(), itemId);
            redirectAttributes.addFlashAttribute("cartSuccess", "Item removed from cart.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:/cart";
    }

    /**
     * Clears all items from the cart.
     */
    @PostMapping("/clear")
    public String clearCart(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            cartService.clearCart(principal.getName());
            redirectAttributes.addFlashAttribute("cartSuccess", "Cart cleared.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:/cart";
    }
}
