package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    protected UUID uuid;

    private LocalDateTime date;
    private BigDecimal subTotal;
    private BigDecimal discount;

    private BigDecimal vat;

    private BigDecimal total;

    private String promoCode;

    private OrderStatus status;

    private BigDecimal remainingAmount;

    @NotNull(message = "L'UUID du client est obligatoire")
    private UUID clientUuid;

    private String clientName;

    @NotNull(message = "La liste des produits est obligatoire")
    @Size(min = 1, message = "La commande doit contenir au moins un produit")
    private List<@Valid OrderItemDTO> orderItems;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
