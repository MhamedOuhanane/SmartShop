package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.User.EmailAlreadyExistsException;
import com.smartshop.smartshop.exception.User.UsernameAlreadyExistsException;
import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ClientCreateDTO;
import com.smartshop.smartshop.model.dto.ClientDTO;
import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.enums.CustomerTier;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.model.mapper.ClientMapper;
import com.smartshop.smartshop.repository.ClientRepository;
import com.smartshop.smartshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {
    @Mock
    private ClientRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientMapper mapper;

    @InjectMocks
    private ClientServiceImpl service;

    @Test
    void create_shouldCreateClientSuccessfully() {

        ClientCreateDTO dto = ClientCreateDTO.builder()
                .username("clientUser")
                .password("123456")
                .email("client@gmail.com")
                .name("Client Nom")
                .build();

        Client client = Client.builder()
                .uuid(UUID.randomUUID())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .name(dto.getName())
                .password("hashed")
                .role(UserRole.CLIENT)
                .loyaltyLevel(CustomerTier.BASIC)
                .build();

        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(client);
        when(repository.save(client)).thenReturn(client);
        when(mapper.toDto(client)).thenReturn(new ClientDTO());

        var result = service.create(dto);

        assertEquals(201, result.getStatus());
        assertEquals("Client ajouté avec succès!", result.getMessage());
        assertNotNull(result.getData());
    }

    @Test
    void create_shouldThrow_whenDtoNull() {
        assertThrows(BadRequestException.class, () -> service.create(null));
    }

    @Test
    void create_shouldThrow_whenEmailExists() {
        ClientCreateDTO dto = ClientCreateDTO.builder()
                .username("clientUser")
                .password("123456")
                .email("client@gmail.com")
                .name("Client Nom")
                .build();

        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new Client()));

        assertThrows(EmailAlreadyExistsException.class, () -> service.create(dto));
    }

    @Test
    void create_shouldThrow_whenUsernameExists() {
        ClientCreateDTO dto = ClientCreateDTO.builder()
                .username("clientUser")
                .password("123456")
                .email("client@gmail.com")
                .name("Client Nom")
                .build();

        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new Client()));

        assertThrows(UsernameAlreadyExistsException.class, () -> service.create(dto));
    }

    @Test
    void update_shouldUpdateSuccessfully() {
        UUID uuid = UUID.randomUUID();

        Client client = Client.builder()
                .uuid(uuid)
                .email("old@gmail.com")
                .username("oldUser")
                .name("Old Name")
                .loyaltyLevel(CustomerTier.BASIC)
                .build();

        ClientDTO dto = ClientDTO.builder()
                .email("new@gmail.com")
                .username("newUser")
                .name("New Name")
                .build();

        when(repository.findByUuid(uuid)).thenReturn(Optional.of(client));
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(client);
        when(mapper.toDto(client)).thenReturn(dto);

        var result = service.update(uuid, dto);

        assertEquals(200, result.getStatus());
        assertTrue(result.getMessage().contains("Les informations du client a été mis à jour avec succès!"));
    }

    @Test
    void update_shouldSuccess_whenNoModification() {
        UUID uuid = UUID.randomUUID();

        Client client = Client.builder()
                .uuid(uuid)
                .email("old@gmail.com")
                .username("oldUser")
                .name("Old Name")
                .loyaltyLevel(CustomerTier.BASIC)
                .build();

        ClientDTO dto = ClientDTO.builder()
                .email(client.getEmail())
                .username(client.getUsername())
                .name(client.getName())
                .build();

        when(repository.findByUuid(uuid)).thenReturn(Optional.of(client));
        when(mapper.toDto(client)).thenReturn(dto);

        var result = service.update(uuid, dto);

        assertEquals(200, result.getStatus());
        assertTrue(result.getMessage().contains("Aucun champ des informations du client n'a été modifié."));
    }

    @Test
    void update_shouldThrow_whenDtoNull() {
        assertThrows(BadRequestException.class, () -> service.update(UUID.randomUUID(), null));
    }

    @Test
    void update_shouldThrow_whenUuidNull() {
        assertThrows(BadRequestException.class, () -> service.update(null, new ClientDTO()));
    }

    @Test
    void update_shouldThrow_whenClientNotFound() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(uuid, new ClientDTO()));
    }

    @Test
    void update_shouldThrow_whenEmailAlreadyExists() {
        UUID uuid = UUID.randomUUID();

        Client client = Client.builder()
                .uuid(uuid)
                .email("old@gmail.com")
                .username("old")
                .name("Old")
                .build();

        ClientDTO dto = ClientDTO.builder()
                .email("new@gmail.com")
                .username("old")
                .name("Old")
                .build();

        when(repository.findByUuid(uuid)).thenReturn(Optional.of(client));
        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new Client()));

        assertThrows(EmailAlreadyExistsException.class, () -> service.update(uuid, dto));
    }

    @Test
    void update_shouldThrow_whenUsernameAlreadyExists() {
        UUID uuid = UUID.randomUUID();

        Client client = Client.builder()
                .uuid(uuid)
                .email("old@gmail.com")
                .username("oldUsername")
                .name("Old")
                .build();

        ClientDTO dto = ClientDTO.builder()
                .email(client.getEmail())
                .username("newUsername")
                .name("Old")
                .build();

        when(repository.findByUuid(uuid)).thenReturn(Optional.of(client));
        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(new Client()));

        assertThrows(UsernameAlreadyExistsException.class, () -> service.update(uuid, dto));
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoClients() {
        Page<Client> emptyPage = new PageImpl<>(List.of());

        when(repository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        var result = service.findAll(0, 5);

        assertTrue(result.getData().isEmpty());
        assertEquals("Aucun client n'existe dans le système", result.getMessage());
    }

    @Test
    void findAll_shouldReturnClientsSuccessfully() {
        Client client = new Client();
        Page<Client> page = new PageImpl<>(List.of(client));

        when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        when(mapper.toDto(any())).thenReturn(new ClientDTO());

        var result = service.findAll(null, null);

        assertFalse(result.getData().isEmpty());
        assertEquals(200, result.getStatus());
    }

    @Test
    void find_shouldReturnClientSuccessfully() {
        UUID uuid = UUID.randomUUID();
        Client client = new Client();

        when(repository.findByUuid(uuid)).thenReturn(Optional.of(client));
        when(mapper.toDto(client)).thenReturn(new ClientDTO());

        var result = service.find(uuid);

        assertEquals(200, result.getStatus());
        assertEquals("Le client trouvés avec succès", result.getMessage());
    }

    @Test
    void find_shouldThrow_whenUuidNull() {
        assertThrows(BadRequestException.class, () -> service.find(null));
    }

    @Test
    void find_shouldThrow_whenClientNotFound() {
        UUID uuid = UUID.randomUUID();
        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.find(uuid));
    }
}
