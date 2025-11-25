package com.smartshop.smartshop.model.mapper;

import com.smartshop.smartshop.model.dto.PaymentDTO;
import com.smartshop.smartshop.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.annotation.Order;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(source = "order.uuid", target = "orderUuid")
    PaymentDTO toDto(Payment payment);

    Payment toEntity(PaymentDTO dto);
}
