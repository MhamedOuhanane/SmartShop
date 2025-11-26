package com.smartshop.smartshop.service.interfaces;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.ProductDTO;
import com.smartshop.smartshop.model.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {
    ApiResponse<ProductDTO> create(ProductDTO dto);
    ApiResponse<ProductDTO> update(UUID uuid, ProductDTO dto);
    ApiResponse<List<ProductDTO>> findAll(Integer page, Integer size, UserRole role);
    ApiResponse<ProductDTO> find(UUID uuid);
    ApiResponse<ProductDTO> softDelete(UUID uuid);
}
