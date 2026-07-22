package com.jk1.security;

import com.jk1.entity.User;
import com.jk1.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private com.jk1.service.SecurityService securityService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        
        if (user.getFailedLoginAttempts() > 0 || user.getLockTime() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        }

        if (user.is2faEnabled()) {
            securityService.generateAndSendOtp(user);
            // In a real flow, we might use a PreAuth filter or store a temp token. 
            // For now, we will redirect to /verify-otp and put a temporary attribute in the session.
            request.getSession().setAttribute("2fa_user_id", user.getId());
            // Clear standard Spring Security Context so they aren't fully logged in yet
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            
            getRedirectStrategy().sendRedirect(request, response, "/verify-otp");
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
