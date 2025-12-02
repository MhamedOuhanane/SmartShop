package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.generic.InvalidCredentialsException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.LoginDto;
import com.smartshop.smartshop.model.dto.UserDTO;
import com.smartshop.smartshop.model.entity.User;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.model.mapper.UserMapper;
import com.smartshop.smartshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    UserRepository repository;

    @Mock
    UserMapper mapper;

    @InjectMocks UserServiceImpl service;

    @Test
    public void login_shouldSucceed_whenLogin() {
        LoginDto login = new LoginDto("MohammedAli", "12345678");
        User user = User.builder()
                        .id(Long.getLong("123"))
                        .uuid(UUID.randomUUID())
                        .username("MohammedAli")
                        .password(BCrypt.hashpw("12345678", BCrypt.gensalt()))
                        .role(UserRole.CLIENT)
                        .build();
        UserDTO dto = UserDTO.builder()
                        .uuid(user.getUuid())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build();

        when(repository.findByUsername(login.getUsername())).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(dto);

        var result = service.login(login);

        assertEquals(LocalDateTime.now(), result.getDate());
        assertEquals(200, result.getStatus());
        assertEquals("Connexion réussie", result.getMessage());
        assertEquals(dto, result.getData());
        assertNull(result.getPath());
        assertNull(result.getPagination());
    }

    @Test
    public void login_shouldThrowException_whenUserNotExist() {
        LoginDto login = new LoginDto("MohammedAli", "12345678");
        when(repository.findByUsername(login.getUsername())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.login(login));

        assertEquals("L'utilisateur avec l'username 'MohammedAli' n'existe pas !", exception.getMessage());
    }

    @Test
    public void login_shouldThrowException_whenPasswordIncorrect() {
        LoginDto login = new LoginDto("MohammedAli", "123456789");
        User user = User.builder()
                .id(Long.getLong("123"))
                .uuid(UUID.randomUUID())
                .username("MohammedAli")
                .password(BCrypt.hashpw("12345678", BCrypt.gensalt()))
                .role(UserRole.CLIENT)
                .build();

        when(repository.findByUsername(login.getUsername())).thenReturn(Optional.of(user));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> service.login(login));

        assertEquals("Username d'utilisateur ou mot de passe incorrect", exception.getMessage());
    }


}
