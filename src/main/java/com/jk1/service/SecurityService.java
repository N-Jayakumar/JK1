package com.jk1.service;

import com.jk1.entity.OtpToken;
import com.jk1.entity.PasswordResetToken;
import com.jk1.entity.User;
import com.jk1.repository.OtpTokenRepository;
import com.jk1.repository.PasswordResetTokenRepository;
import com.jk1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void generateAndSendOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        OtpToken otpToken = otpTokenRepository.findByUser(user).orElse(new OtpToken());
        otpToken.setUser(user);
        otpToken.setToken(otp);
        otpToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        otpTokenRepository.save(otpToken);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your Login OTP");
        message.setText("Your OTP is: " + otp + "\nIt expires in 5 minutes.");
        // Try-catch block just in case email config is not set up correctly to prevent breaking login flow entirely
        try {
            if (mailSender != null) {
                mailSender.send(message);
            } else {
                log.warn("MailSender is disabled/null. OTP: {}", otp);
            }
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", user.getEmail(), e);
        }
    }

    public boolean verifyOtp(Long userId, String token) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        Optional<OtpToken> otpTokenOpt = otpTokenRepository.findByUser(user);
        if (otpTokenOpt.isPresent()) {
            OtpToken otpToken = otpTokenOpt.get();
            if (otpToken.getToken().equals(token) && !otpToken.isExpired()) {
                otpTokenRepository.delete(otpToken);
                return true;
            }
        }
        return false;
    }

    public void createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(myToken);
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reset Password");
        // Uses environment variable or properties for base url
        message.setText("To reset your password, click the link below:\n" +
                baseUrl + "/reset-password?token=" + token);
        try {
            if (mailSender != null) {
                mailSender.send(message);
            } else {
                log.warn("MailSender is disabled/null. Password Reset Token: {}", token);
            }
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", user.getEmail(), e);
        }
    }

    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passTokenOpt = passwordResetTokenRepository.findByToken(token);
        if (!passTokenOpt.isPresent()) {
            return "invalidToken";
        }
        PasswordResetToken passToken = passTokenOpt.get();
        if (passToken.isExpired()) {
            return "expired";
        }
        return null; // Valid
    }

    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> passTokenOpt = passwordResetTokenRepository.findByToken(token);
        if (passTokenOpt.isPresent()) {
            PasswordResetToken passToken = passTokenOpt.get();
            User user = passToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            passwordResetTokenRepository.delete(passToken);
        }
    }
}
