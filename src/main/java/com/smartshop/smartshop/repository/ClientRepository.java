package com.smartshop.smartshop.repository;

import com.smartshop.smartshop.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByUuid(UUID uuid);
    Optional<Client> findByEmail(String email);
    Optional<Client> findByUsername(String email);
}
