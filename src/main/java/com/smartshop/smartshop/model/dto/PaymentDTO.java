package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.entity.Auditable;
import com.smartshop.smartshop.model.entity.Order;
import com.smartshop.smartshop.model.enums.PaymentStatus;
import com.smartshop.smartshop.model.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Integer paymentNumber;

    private BigDecimal amount;

    private PaymentType paymentType;

    private LocalDateTime paymentDate = LocalDateTime.now();

    private LocalDateTime collectionDate;

    private PaymentStatus status = PaymentStatus.PENDING;

    private UUID orderUuid;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
