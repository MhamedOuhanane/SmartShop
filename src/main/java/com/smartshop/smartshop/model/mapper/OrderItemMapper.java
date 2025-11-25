package com.smartshop.smartshop.model.mapper;

import com.smartshop.smartshop.model.dto.OrderItemDTO;
import com.smartshop.smartshop.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(source = "product.uuid", target = "productUuid")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "order.uuid", target = "orderUuid")
    OrderItemDTO toDto(OrderItem orderItem);

    OrderItem toEntity(OrderItemDTO dto);
}
