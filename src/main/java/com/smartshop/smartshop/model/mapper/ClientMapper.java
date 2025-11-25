package com.smartshop.smartshop.model.mapper;

import com.smartshop.smartshop.model.dto.ClientDTO;
import com.smartshop.smartshop.model.entity.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDTO toDto(Client client);
    Client toEntity(ClientDTO dto);
}
