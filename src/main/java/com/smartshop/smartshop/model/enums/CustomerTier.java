package com.smartshop.smartshop.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerTier {
    BASIC("Basique"),
    SILVER("Argent"),
    GOLD("Or"),
    PLATINUM("Platine");

    private final String description;
}
