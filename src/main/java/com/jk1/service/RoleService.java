package com.jk1.service;

import com.jk1.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role save(Role role);
    Optional<Role> findById(Long id);
    List<Role> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
