package com.jk1.service.impl;

import com.jk1.dto.request.RegistrationRequest;
import com.jk1.entity.Role;
import com.jk1.entity.User;
import com.jk1.entity.enums.AccountStatus;
import com.jk1.entity.enums.UserRole;
import com.jk1.exception.DuplicateResourceException;
import com.jk1.repository.RoleRepository;
import com.jk1.repository.UserRepository;
import com.jk1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public User updateProfile(String email, com.jk1.dto.request.UserRequestDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());

        return userRepository.save(user);
    }

    @Override
    public void changePassword(String email, com.jk1.dto.request.PasswordChangeDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Full registration flow:
     * 1. Validates password confirmation match
     * 2. Checks email uniqueness (throws DuplicateResourceException on conflict)
     * 3. Splits fullName into firstName + lastName (last word = lastName)
     * 4. BCrypt-encodes the password
     * 5. Assigns ROLE_CUSTOMER (must exist in DB — seeded by DataInitializer on startup)
     * 6. Sets accountStatus = ACTIVE
     * 7. Persists and returns the saved User
     */
    @Override
    @Transactional
    public User registerUser(RegistrationRequest request) {

        // 1. Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match. Please try again.");
        }

        // 2. Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException(
                    "An account with email '" + request.getEmail() + "' already exists.");
        }

        // 3. Split fullName → firstName + lastName
        String fullName = request.getFullName().trim();
        String firstName;
        String lastName;
        int lastSpace = fullName.lastIndexOf(' ');
        if (lastSpace > 0) {
            firstName = fullName.substring(0, lastSpace).trim();
            lastName  = fullName.substring(lastSpace + 1).trim();
        } else {
            // Single-word name — use as firstName, "." as lastName placeholder
            firstName = fullName;
            lastName  = ".";
        }

        // 4. Lookup ROLE_CUSTOMER — must exist (seeded by DataInitializer on startup)
        Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException(
                        "ROLE_CUSTOMER not found in database. Ensure the data initializer ran successfully."));

        // 5. Build the user
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .roles(roles)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }
}
