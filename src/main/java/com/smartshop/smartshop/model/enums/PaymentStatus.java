package com.smartshop.smartshop.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("En attente"),
    COMPLETED("Encaisse"),
    REJECTED("Rejeté");

    private final String description;
}