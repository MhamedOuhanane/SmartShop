package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    protected UUID uuid;

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
    private Set<OrderItemDTO> orderItems;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
