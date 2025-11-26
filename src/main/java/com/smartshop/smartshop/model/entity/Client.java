package com.smartshop.smartshop.model.entity;

import com.smartshop.smartshop.model.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_level", nullable = false)
    private CustomerTier loyaltyLevel = CustomerTier.BASIC;

    @Builder.Default
    @OneToMany(mappedBy = "client")
    private Set<Order> orders = new HashSet<>();


}
