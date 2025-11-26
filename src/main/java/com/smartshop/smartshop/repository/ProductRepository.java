package com.smartshop.smartshop.repository;

import com.smartshop.smartshop.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByName(String name);
    Optional<Product> findByUuid(UUID uuid);

    @Query("Select p From Product p Where p.uuid = :uuid And p.deleted = true")
    Optional<Product> findDeletedByUuid(@Param("uuid") UUID uuid);

    @Query("Select p From Product p Where p.deleted = true")
    Page<Product> findAllDeleted(Pageable pageable);

    @Modifying
    @Query("Update p From Product p Set p.deleted = true Where p.uuid = :uuid")
    void restoreDeleted(@Param("uuid") UUID uuid);

}
