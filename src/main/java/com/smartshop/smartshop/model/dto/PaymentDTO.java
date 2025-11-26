package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.enums.PaymentStatus;
import com.smartshop.smartshop.model.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    protected UUID uuid;

    private Integer paymentNumber;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    @NotNull(message = "Le type de paiement est obligatoire")
    private PaymentType paymentType;

    private LocalDateTime paymentDate;

    private LocalDateTime collectionDate;

    private PaymentStatus status;

    @NotNull(message = "L'UUID de la commande est obligatoire")
    private UUID orderUuid;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
