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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(PaymentDTO dto);
}
