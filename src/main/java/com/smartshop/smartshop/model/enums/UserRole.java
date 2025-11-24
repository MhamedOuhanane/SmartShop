package com.smartshop.smartshop.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("Administrateur"),
    CLIENT("Client");

    private final String description;
}
