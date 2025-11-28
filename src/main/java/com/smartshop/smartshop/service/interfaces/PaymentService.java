package com.smartshop.smartshop.service.interfaces;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.PaymentDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    ApiResponse<PaymentDTO> create(PaymentDTO dto);
    ApiResponse<List<PaymentDTO>> findAll(Integer page, Integer size);
    ApiResponse<List<PaymentDTO>> findOrderPayments(UUID uuid, Integer page, Integer size);
}
