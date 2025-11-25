package com.smartshop.smartshop.model.mapper;

import com.smartshop.smartshop.model.dto.OrderDTO;
import com.smartshop.smartshop.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "client.uuid", target = "clientUuid")
    @Mapping(source = "client.name", target = "clientName")
    OrderDTO toDto(Order order);

    Order toEntity(OrderDTO dto);
}
