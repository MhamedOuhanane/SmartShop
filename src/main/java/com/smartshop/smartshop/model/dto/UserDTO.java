package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.entity.Auditable;
import com.smartshop.smartshop.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    protected UUID uuid;

    @NotNull(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 6, max = 50, message = "Le nom d'utilisateur doit être compris entre 6 et 50 caractères")
    protected String username;

//    @NotNull(message = "Le rôle est obligatoire")
    protected UserRole role;

    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
