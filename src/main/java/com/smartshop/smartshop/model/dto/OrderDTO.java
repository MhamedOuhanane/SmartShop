package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.enums.OrderStatus;
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
    private LocalDateTime date = LocalDateTime.now();

    private BigDecimal subTotal;

    private BigDecimal discount;

    private BigDecimal vat;

    private BigDecimal total;

    private String promoCode;

    private OrderStatus status;

    private BigDecimal remainingAmount;

    private UUID clientUuid;
    private String clientName;
    private Set<OrderItemDTO> orderItems = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
