package com.jk1.controller;

import com.jk1.service.AddressService;
import com.jk1.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final AddressService addressService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        String email = principal.getName();
        model.addAttribute("cart", cartService.getCartForUser(email));
        model.addAttribute("addresses", addressService.findUserAddresses(email));
        
        return "checkout";
    }
}
