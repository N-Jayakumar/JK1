package com.jk1.config;

import com.jk1.entity.Role;
import com.jk1.entity.User;
import com.jk1.entity.enums.AccountStatus;
import com.jk1.entity.enums.UserRole;
import com.jk1.repository.RoleRepository;
import com.jk1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Runs once at application startup to seed required reference data:
 *
 *   Roles   : ROLE_ADMIN, ROLE_CUSTOMER, ROLE_SELLER
 *   Admin   : admin@jk0.com / Admin@123  (ROLE_ADMIN, ACTIVE)
 *
 * Idempotent — safe to run on every restart.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedRoles();
        seedAdminUser();
    }

    // ─── Roles ─────────────────────────────────────────────────────────────────

    private void seedRoles() {
        for (UserRole roleEnum : UserRole.values()) {
            if (roleRepository.findByName(roleEnum).isEmpty()) {
                Role role = Role.builder()
                        .name(roleEnum)
                        .build();
                roleRepository.save(role);
                log.info("[DataInitializer] Created role: {}", roleEnum.name());
            }
        }
    }

    // ─── Admin user ────────────────────────────────────────────────────────────

    private static final String ADMIN_EMAIL    = "admin@jk0.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private void seedAdminUser() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.debug("[DataInitializer] Admin user already exists — skipping.");
            return;
        }

        Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_ADMIN missing after role seed — this should never happen"));

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User admin = User.builder()
                .firstName("Admin")
                .lastName("JKO")
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .accountStatus(AccountStatus.ACTIVE)
                .roles(roles)
                .build();

        userRepository.save(admin);
        log.info("[DataInitializer] Admin user created: {} (password encoded with BCrypt)", ADMIN_EMAIL);
    }
}
