package com.jk1.controller;

import com.jk1.dto.request.RegistrationRequest;
import com.jk1.exception.DuplicateResourceException;
import com.jk1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles authentication-related view endpoints.
 *
 *  GET  /login    — custom JKØ login page (Spring Security processes POST /login)
 *  GET  /register — registration form
 *  POST /register — registration form submission
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // ─── Login ─────────────────────────────────────────────────────────────────

    /**
     * Serves the custom JKØ login page.
     * Spring Security's formLogin processes POST /login automatically.
     *
     * @param error  present when login failed  (?error)
     * @param logout present after logout        (?logout)
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error",  required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    // ─── Register ──────────────────────────────────────────────────────────────

    /** Serves the registration page with a blank form object. */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "auth/register";
    }

    /**
     * Processes the registration form submission.
     *
     * Validation order:
     *  1. JSR-303 field-level validation (@Valid)
     *  2. Password confirmation match
     *  3. Email uniqueness (DuplicateResourceException)
     *
     * On success → redirect to /login?registered
     * On failure → re-render the register page with errorMessage
     */
    @PostMapping("/register")
    public String handleRegistration(
            @Valid @ModelAttribute("registrationRequest") RegistrationRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. JSR-303 validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(fe -> fe.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Please fix the form errors.");
            model.addAttribute("errorMessage", errors);
            return "auth/register";
        }

        // 2. Password confirmation check (belt-and-suspenders — also done in service)
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Passwords do not match. Please try again.");
            return "auth/register";
        }

        try {
            userService.registerUser(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account created successfully! Please sign in.");
            return "redirect:/login?registered";

        } catch (DuplicateResourceException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";

        } catch (Exception ex) {
            log.error("[AuthController] Registration failed for email={}: {}",
                    request.getEmail(), ex.getMessage(), ex);
            model.addAttribute("errorMessage",
                    "Registration failed: " + ex.getMessage());
            return "auth/register";
        }
    }
}
