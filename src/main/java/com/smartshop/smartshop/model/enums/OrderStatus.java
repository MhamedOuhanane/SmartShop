package com.smartshop.smartshop.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    CANCELED("Annulée"),
    REJECTED("Rejetée");

    private final String description;
}
