package com.smartshop.smartshop.service.interfaces;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.ClientDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ClientService {
    ApiResponse<ClientDTO> create(ClientDTO dto);
    ApiResponse<ClientDTO> update(UUID uuid, ClientDTO dto);
    ApiResponse<List<ClientDTO>> findAll(Integer page, Integer size);
    ApiResponse<ClientDTO> find(UUID uuid);
}
