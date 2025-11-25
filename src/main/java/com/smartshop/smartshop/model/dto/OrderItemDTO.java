package com.smartshop.smartshop.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;

    private UUID orderUuid;

    @NotNull(message = "L'UUID du produit est obligatoire")
    private UUID productUuid;

    private String productName;

    private BigDecimal unitPrice;

    private BigDecimal totalLine;

}
