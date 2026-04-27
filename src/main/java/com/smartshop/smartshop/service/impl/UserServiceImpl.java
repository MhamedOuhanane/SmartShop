package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.User.EmailAlreadyExistsException;
import com.smartshop.smartshop.exception.generic.InvalidCredentialsException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.LoginDto;
import com.smartshop.smartshop.model.dto.RegisterDto;
import com.smartshop.smartshop.model.dto.UserDTO;
import com.smartshop.smartshop.model.entity.User;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.model.mapper.UserMapper;
import com.smartshop.smartshop.repository.UserRepository;
import com.smartshop.smartshop.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;


    @Override
    public ApiResponse<UserDTO> login(LoginDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new NotFoundException("L'utilisateur avec l'username '" + dto.getUsername() + "' n'existe pas !"));

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Username d'utilisateur ou mot de passe incorrect");
        }

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Connexion réussie",
                200,
                mapper.toDto(user),
                null,
                null
        );
    }

    @Override
    public ApiResponse<Void> register(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new EmailAlreadyExistsException(String.format("Username '%s' already exists", dto.getUsername()));

        User user = User.builder()
                .username(dto.getUsername())
                .role(UserRole.CLIENT)
                .build();

        user.setPassword(dto.getPassword());

        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .date(LocalDateTime.now())
                .message("Inscription réussie")
                .status(201)
                .data(null)
                .build();
    }
}
