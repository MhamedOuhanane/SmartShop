package com.smartshop.smartshop.model.mapper;

import com.smartshop.smartshop.model.dto.UserDTO;
import com.smartshop.smartshop.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
}
