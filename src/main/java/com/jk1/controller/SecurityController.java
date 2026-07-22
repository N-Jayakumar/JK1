package com.jk1.controller;

import com.jk1.entity.User;
import com.jk1.repository.UserRepository;
import com.jk1.security.CustomUserDetails;
import com.jk1.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;
    private final UserRepository userRepository;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            securityService.createPasswordResetTokenForUser(userOpt.get());
        }
        // Always show success message to prevent email enumeration
        model.addAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        String result = securityService.validatePasswordResetToken(token);
        if (result != null) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "login"; // Redirect or show error
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, 
                                       @RequestParam("password") String password,
                                       Model model) {
        String result = securityService.validatePasswordResetToken(token);
        if (result != null) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "login";
        }
        
        // Basic password validation could be added here
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        securityService.resetPassword(token, password);
        model.addAttribute("message", "Password successfully reset. You can now log in.");
        return "login";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(HttpSession session) {
        if (session.getAttribute("2fa_user_id") == null) {
            return "redirect:/login";
        }
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(@RequestParam("otp") String otp, HttpSession session, HttpServletRequest request) {
        Long userId = (Long) session.getAttribute("2fa_user_id");
        if (userId == null) {
            return "redirect:/login";
        }

        if (securityService.verifyOtp(userId, otp)) {
            // OTP is valid. Log the user in.
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                CustomUserDetails userDetails = new CustomUserDetails(user);
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                // In Spring Session, setting the attribute explicitly creates a session
                request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                session.removeAttribute("2fa_user_id");
                return "redirect:/";
            }
        }
        return "redirect:/verify-otp?error";
    }
}
