package com.jk1.service;

import com.jk1.entity.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    Page<User> findAll(Specification<User> spec, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
    User updateProfile(String email, com.jk1.dto.request.UserRequestDTO dto);
    void changePassword(String email, com.jk1.dto.request.PasswordChangeDTO dto);

    /**
     * Handles the full registration flow: validates uniqueness, splits fullName,
     * encodes the password, assigns ROLE_CUSTOMER, and persists the user.
     */
    User registerUser(com.jk1.dto.request.RegistrationRequest request);
}
