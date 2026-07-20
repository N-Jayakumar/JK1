package com.jk1.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Static resources — always permit
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/icons/**", "/uploads/**", "/webjars/**").permitAll()
                // Public pages
                .requestMatchers("/", "/home", "/login", "/register", "/products/**", "/deals", "/deals/**",
                                 "/search", "/api/v1/search/**", "/api/search/**").permitAll()
                // Role-based access
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/seller/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers("/account/**", "/cart/**", "/checkout/**").hasAnyRole("CUSTOMER", "ADMIN", "SELLER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // Tell Spring Security to use our custom Thymeleaf login page
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                .key("jk1-secret-key-for-remember-me")
                .tokenValiditySeconds(86400 * 30) // 30 days
                .userDetailsService(userDetailsService)
                .rememberMeParameter("remember-me")
            )
            // Use cookie-based CSRF to avoid "Cannot create session after response committed"
            // which happens with the default HttpSessionCsrfTokenRepository when Thymeleaf
            // flushes a large template response before reaching the form's th:action tag.
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
            );

        return http.build();
    }
}
