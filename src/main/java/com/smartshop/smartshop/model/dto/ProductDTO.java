package com.smartshop.smartshop.model.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    protected UUID uuid;

    @NotNull(message = "Le nom du produit est obligatoire")
    @Size(min = 4, max = 100, message = "Le nom du produit doit avoir entre 2 et 100 caractères")
    private String name;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal price;

    @NotNull(message = "Le stock est obligatoire")
    @Positive(message = "Le stock ne peut pas être négatif ou égal a 0")
    private Integer stock;

    @NotNull(message = "Le pourcentage de TVA est obligatoire")
    @DecimalMin(value = "0.01", inclusive = true, message = "Le pourcentage de TVA doit être supérieur à 0")
    @DecimalMax(value = "1", inclusive = true, message = "Le pourcentage de TVA ne peut pas dépasser 1")
    private BigDecimal prcTVA;

    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
