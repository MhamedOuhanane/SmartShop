package com.smartshop.smartshop.model.entity;

import com.smartshop.smartshop.model.enums.CustomerTier;
import com.smartshop.smartshop.model.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Order extends Auditable{
    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;


    @Column(nullable = false, precision = 5, scale = 3)
    private BigDecimal discount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal vat;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "promo_code")
    private String promoCode;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "remaining_amount", precision = 10, scale = 2)
    private BigDecimal remainingAmount;

    @Builder.Default
    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "order")
    private Set<Payment> payments = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;


    @PrePersist
    public void calculate() {
        subTotal = orderItems.stream()
                .map(OrderItem::getTotalLine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CustomerTier tier = client != null ? client.getLoyaltyLevel() : CustomerTier.BASIC;

        if (tier == CustomerTier.BASIC) {
            discount = BigDecimal.ZERO;
        } else if (tier == CustomerTier.SILVER) {
            discount = subTotal.compareTo(BigDecimal.valueOf(500)) >= 0
                    ? BigDecimal.valueOf(0.05)
                    : BigDecimal.ZERO;
        } else if (tier == CustomerTier.GOLD) {
            if (subTotal.compareTo(BigDecimal.valueOf(800)) >= 0)
                discount = BigDecimal.valueOf(0.10);
            else if (subTotal.compareTo(BigDecimal.valueOf(500)) >= 0)
                discount = BigDecimal.valueOf(0.05);
            else
                discount = BigDecimal.ZERO;
        } else {
            if (subTotal.compareTo(BigDecimal.valueOf(1200)) >= 0)
                discount = BigDecimal.valueOf(0.15);
            else if (subTotal.compareTo(BigDecimal.valueOf(800)) >= 0)
                discount = BigDecimal.valueOf(0.10);
            else if (subTotal.compareTo(BigDecimal.valueOf(500)) >= 0)
                discount = BigDecimal.valueOf(0.05);
            else
                discount = BigDecimal.ZERO;
        }

        BigDecimal discountAmount = subTotal.multiply(discount);
        BigDecimal ht = subTotal.subtract(discountAmount);

        BigDecimal TVA = BigDecimal.valueOf(0.20);
        vat = ht.multiply(TVA);
        total = ht.add(vat);
        remainingAmount = total;
    }


}
