package com.jk1.controller;

import com.jk1.dto.request.PasswordChangeDTO;
import com.jk1.dto.request.UserRequestDTO;
import com.jk1.entity.User;
import com.jk1.repository.UserRepository;
import com.jk1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository; // For reading the current user

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        return "customer/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        
        model.addAttribute("userDto", dto);
        return "customer/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("userDto") UserRequestDTO dto, 
                                BindingResult result, 
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        if (result.hasErrors()) {
            return "customer/profile-edit";
        }
        
        try {
            userService.updateProfile(principal.getName(), dto);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/account/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/account/profile/edit";
        }
    }

    @GetMapping("/settings")
    public String settingsForm(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        model.addAttribute("passwordDto", new PasswordChangeDTO());
        return "customer/settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(@Valid @ModelAttribute("passwordDto") PasswordChangeDTO dto,
                                 BindingResult result,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) return "redirect:/login";
        
        if (result.hasErrors()) {
            return "customer/settings";
        }
        
        try {
            userService.changePassword(principal.getName(), dto);
            redirectAttributes.addFlashAttribute("success", "Password updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/account/settings";
    }
}
