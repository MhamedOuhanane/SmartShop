package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.entity.Auditable;
import com.smartshop.smartshop.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID uuid;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
