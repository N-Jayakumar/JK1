package com.jk1.repository;

import com.jk1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
