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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderDTO dto);
}
