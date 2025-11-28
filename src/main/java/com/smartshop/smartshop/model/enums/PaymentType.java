package com.smartshop.smartshop.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {
    CASH("Espèces"),
    CHEQUE("Chèque"),
    TRANSFER("Virement");

    private final String description;
}
