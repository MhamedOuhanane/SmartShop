package com.smartshop.smartshop.model.entity;

import com.smartshop.smartshop.model.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "remaining_amount", precision = 10, scale = 2)
    private BigDecimal remainingAmount;

    @PrePersist
    public void a() {
        BigDecimal amountDiscount = (discount != null ? subTotal.multiply(discount) : BigDecimal.ZERO);
        BigDecimal amountHt = subTotal.subtract(amountDiscount);
        BigDecimal tvaRate = new BigDecimal("0.20");

        vat = amountHt.multiply(tvaRate);
        total = subTotal.add(vat);
        remainingAmount = total;
    }

}
