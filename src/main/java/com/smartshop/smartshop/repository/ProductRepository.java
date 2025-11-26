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

    @Query("Select p From Product p Where p.uuid = :uuid And p.deletedAt Is Null")
    Optional<Product> findByUuid(UUID uuid);

    @Query("Select p From Product p Where p.deletedAt Is Null")
    Page<Product> findAllProduct(Pageable pageable);

    @Query("Select p From Product p Where p.uuid = :uuid And p.deletedAt Is Not Null")
    Optional<Product> findDeletedByUuid(@Param("uuid") UUID uuid);

    @Query("Select p From Product p Where p.deletedAt Is Not Null")
    Page<Product> findAllDeleted(Pageable pageable);

    @Modifying
    @Query("Update Product p Set p.deletedAt = Null Where p.uuid = :uuid")
    void restoreDeleted(@Param("uuid") UUID uuid);

}
