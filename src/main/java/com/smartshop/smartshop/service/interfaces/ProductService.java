package com.smartshop.smartshop.service.interfaces;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ApiResponse<ProductDTO> create(ProductDTO dto);
    ApiResponse<ProductDTO> update(UUID uuid, ProductDTO dto);
    ApiResponse<List<ProductDTO>> findAll(Integer page, Integer size);
    ApiResponse<ProductDTO> find(UUID uuid);
    ApiResponse<ProductDTO> softDelete(UUID uuid);
    ApiResponse<ProductDTO> restore(UUID uuid);
    ApiResponse<List<ProductDTO>> findAllDeleted(Integer page, Integer size);
    ApiResponse<ProductDTO> findDeleted(UUID uuid);
    ApiResponse<List<ProductDTO>> MiseSituiation();
}
