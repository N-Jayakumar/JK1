package com.jk1.controller;

import com.jk1.dto.request.AddressRequestDTO;
import com.jk1.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/account/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public String listAddresses(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        model.addAttribute("addresses", addressService.findUserAddresses(principal.getName()));
        return "customer/address-book";
    }

    @GetMapping("/new")
    public String showAddAddressForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        model.addAttribute("addressDto", new AddressRequestDTO());
        return "customer/address-form";
    }

    @PostMapping("/save")
    public String saveAddress(@Valid @ModelAttribute("addressDto") AddressRequestDTO dto, 
                              BindingResult result, 
                              Principal principal,
                              Model model) {
        if (principal == null) return "redirect:/login";
        
        if (result.hasErrors()) {
            return "customer/address-form";
        }
        
        addressService.addAddress(principal.getName(), dto);
        return "redirect:/account/addresses";
    }

    @PostMapping("/{id}/default")
    public String setDefaultAddress(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        addressService.setDefaultAddress(principal.getName(), id);
        return "redirect:/account/addresses";
    }

    @PostMapping("/{id}/delete")
    public String deleteAddress(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        addressService.deleteAddress(principal.getName(), id);
        return "redirect:/account/addresses";
    }
}
