package com.smartshop.smartshop.service.interfaces;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.LoginDto;
import com.smartshop.smartshop.model.dto.RegisterDto;
import com.smartshop.smartshop.model.dto.UserDTO;

public interface UserService {
    ApiResponse<UserDTO> login(LoginDto dto);
    ApiResponse<Void> register(RegisterDto dto);
}
