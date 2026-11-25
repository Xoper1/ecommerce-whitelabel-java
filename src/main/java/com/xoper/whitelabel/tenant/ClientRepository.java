package com.xoper.whitelabel.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByDomainIgnoreCase(String domain);
}