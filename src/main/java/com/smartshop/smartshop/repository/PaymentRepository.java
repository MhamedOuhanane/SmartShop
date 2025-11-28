package com.smartshop.smartshop.repository;

import com.smartshop.smartshop.model.entity.Order;
import com.smartshop.smartshop.model.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findByOrder(Order order, Pageable pageable);
    List<Payment> findAllByOrder(Order order);

    @Query("Select count(p) From Payment p Where p.order.uuid = :uuid")
    Long countByOrderUuid(UUID uuid);
}
