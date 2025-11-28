package com.smartshop.smartshop.service.interfaces;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.LoginDto;
import com.smartshop.smartshop.model.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UserService {
    ApiResponse<UserDTO> login(LoginDto dto);
}
