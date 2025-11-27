package com.smartshop.smartshop.repository;

import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByUuid(UUID uuid);
    Page<Order> findByClient(Client client, Pageable pageable);
    Set<Order> findAllByClient(Client client);
}
