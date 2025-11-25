package com.smartshop.smartshop.model.entity;

import com.smartshop.smartshop.model.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clients")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Client extends User{
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_level", nullable = false)
    private CustomerTier loyaltyLevel;

}
