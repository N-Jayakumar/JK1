package com.jk1.repository;

import com.jk1.entity.OtpToken;
import com.jk1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByToken(String token);
    Optional<OtpToken> findByUser(User user);
    void deleteByUser(User user);
}
