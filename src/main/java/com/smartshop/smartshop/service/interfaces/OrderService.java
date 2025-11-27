package com.smartshop.smartshop.service.interfaces;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.OrderDTO;
import com.smartshop.smartshop.model.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    ApiResponse<OrderDTO> create(OrderDTO dto);
    ApiResponse<OrderDTO> updateStatus(UUID uuid, OrderStatus status);
    ApiResponse<List<OrderDTO>> findAll(Integer page, Integer size);
    ApiResponse<OrderDTO> find(UUID uuid);
    ApiResponse<List<OrderDTO>> findClientOrders(UUID uuid, Integer page, Integer size);
    ApiResponse<?> findClientStatistics(UUID uuid);
}
