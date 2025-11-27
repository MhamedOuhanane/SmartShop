package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.User.EmailAlreadyExistsException;
import com.smartshop.smartshop.exception.User.UsernameAlreadyExistsException;
import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.ClientCreateDTO;
import com.smartshop.smartshop.model.dto.ClientDTO;
import com.smartshop.smartshop.model.dto.PaginationDTO;
import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.enums.CustomerTier;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.model.mapper.ClientMapper;
import com.smartshop.smartshop.repository.ClientRepository;
import com.smartshop.smartshop.repository.UserRepository;
import com.smartshop.smartshop.service.interfaces.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<ClientDTO> create(ClientCreateDTO dto) {
        if (dto == null)
            throw new BadRequestException("Les informations du client ne peuvent pas être vides");

        if (repository.findByEmail(dto.getEmail()).isPresent())
                throw new EmailAlreadyExistsException("Un Client avec l'email '" + dto.getEmail() + "' existe déjà.");

        if (userRepository.findByUsername(dto.getUsername()).isPresent())
                throw new UsernameAlreadyExistsException("Un Client avec Username '" + dto.getUsername() + "' existe déjà.");

        Client client = mapper.toEntity(dto);
        client.setLoyaltyLevel(CustomerTier.BASIC);
        client.setRole(UserRole.CLIENT);
        client = repository.save(client);

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Client ajouté avec succès!",
                201,
                mapper.toDto(client),
                null,
                null
        );
    }

    @Override
    public ApiResponse<ClientDTO> update(UUID uuid, ClientDTO dto) {
        if (dto == null)
            throw new BadRequestException("Les informations du client ne peuvent pas être vides");
        if (uuid == null)
            throw new BadRequestException("UUID du client ne peuvent pas être vides");

        Client client = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun client trouvé avec cet identifiant"));

        boolean updated = false;

        if (!client.getEmail().equals(dto.getEmail())) {
            if (repository.findByEmail(dto.getEmail()).isPresent())
                throw new EmailAlreadyExistsException("Un Client avec l'email '" + dto.getEmail() + "' existe déjà.");
            client.setEmail(dto.getEmail());
            updated = true;
        }

        if (!client.getUsername().equals(dto.getUsername())) {
            if (userRepository.findByUsername(dto.getUsername()).isPresent())
                throw new UsernameAlreadyExistsException("Un Client avec Username '" + dto.getUsername() + "' existe déjà.");
            client.setUsername(dto.getUsername());
            updated = true;
        }

        if (!client.getName().equals(dto.getName())) {
            client.setName(dto.getName());
            updated = true;
        }

        if (updated)
            client = repository.save(client);

        return new ApiResponse<>(
                LocalDateTime.now(),
                updated
                        ? "Les informations du client a été mis à jour avec succès!"
                        : "Aucun champ des informations du client n'a été modifié.",
                200,
                mapper.toDto(client),
                null,
                null
        );

    }

    @Override
    public ApiResponse<List<ClientDTO>> findAll(Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 0 : size;
        Page<Client> clients = repository.findAll(PageRequest.of(page, size, Sort.by("name").ascending()));
        String message;
        List<ClientDTO> data;
        if (clients.getContent().isEmpty()) {
            message = "Aucun client n'existe dans le système";
            data = List.of();
        } else {
            message = "Les clients trouvés avec succès";
            data = clients.stream()
                    .map(mapper::toDto)
                    .toList();
        }
        PaginationDTO pagination = new PaginationDTO(
                clients.getNumber(),
                clients.getSize(),
                clients.getTotalElements(),
                clients.getTotalPages(),
                clients.isFirst(),
                clients.isLast()
        );

        return new ApiResponse<>(
                LocalDateTime.now(),
                message,
                200,
                data,
                null,
                pagination
        );
    }

    @Override
    public ApiResponse<ClientDTO> find(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("UUID du client ne peuvent pas être vides");

        Client client = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun client trouvé avec cet identifiant"));

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Le client trouvés avec succès",
                200,
                mapper.toDto(client),
                null,
                null
        );
    }
}
