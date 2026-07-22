package com.jk1.security;

import com.jk1.entity.User;
import com.jk1.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final long LOCK_TIME_DURATION_MINUTES = 15;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String email = request.getParameter("email");
        if (email != null) {
            userRepository.findByEmail(email).ifPresent(user -> {
                if (user.getLockTime() == null || user.getLockTime().isBefore(LocalDateTime.now())) {
                    int attempts = user.getFailedLoginAttempts() + 1;
                    user.setFailedLoginAttempts(attempts);
                    
                    if (attempts >= MAX_FAILED_ATTEMPTS) {
                        user.setLockTime(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION_MINUTES));
                    }
                    userRepository.save(user);
                }
            });
        }
        
        String redirectUrl = "/login?error";
        if (exception instanceof LockedException) {
            redirectUrl = "/login?locked";
        }
        
        super.setDefaultFailureUrl(redirectUrl);
        super.onAuthenticationFailure(request, response, exception);
    }
}
