package com.smartshop.smartshop.model.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderItemDTO {
    private Integer quantity;

    private UUID orderUuid;

    private UUID productUuid;

    private String productName;

    private BigDecimal unitPrice;

    private BigDecimal totalLine;

}
