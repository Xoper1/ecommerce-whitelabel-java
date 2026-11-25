package com.xoper.whitelabel.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClientIdAndEmailIgnoreCase(Long clientId, String email);
    boolean existsByClientIdAndEmailIgnoreCase(Long clientId, String email);
}