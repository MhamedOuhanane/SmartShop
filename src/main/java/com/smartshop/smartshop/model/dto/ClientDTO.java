package com.smartshop.smartshop.model.dto;

import com.smartshop.smartshop.model.enums.CustomerTier;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientDTO extends UserDTO {
    private String name;

    private String email;

    private CustomerTier loyaltyLevel;

}
