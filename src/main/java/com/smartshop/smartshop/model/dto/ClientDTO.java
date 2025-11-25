package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.enums.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientDTO extends UserDTO {
    @NotNull(message = "Le nom est obligatoire")
    @Size(min = 6, max = 100, message = "Le nom doit avoir entre 6 et 100 caractères")
    private String name;

    @NotNull(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    private CustomerTier loyaltyLevel;
}
