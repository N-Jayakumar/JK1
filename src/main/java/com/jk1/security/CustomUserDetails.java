package com.jk1.security;

import com.jk1.entity.User;
import com.jk1.entity.enums.AccountStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    // Store only primitive/serializable fields instead of the full JPA entity
    // to prevent NotSerializableException and LazyInitializationException
    private final Long id;
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final boolean isAccountNonLocked;
    private final boolean isEnabled;
    private final Collection<? extends GrantedAuthority> authorities;

    // We keep a transient reference to the User object for the SuccessHandler,
    // but it will NOT be serialized by Spring Session.
    private transient User user;

    public CustomUserDetails(User user) {
        this.user = user;
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        
        boolean notSuspendedOrDeleted = user.getAccountStatus() != AccountStatus.SUSPENDED && user.getAccountStatus() != AccountStatus.DELETED;
        boolean notLockedByAttempts = user.getLockTime() == null || user.getLockTime().isBefore(java.time.LocalDateTime.now());
        this.isAccountNonLocked = notSuspendedOrDeleted && notLockedByAttempts;
        this.isEnabled = user.getAccountStatus() == AccountStatus.ACTIVE;
        
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
