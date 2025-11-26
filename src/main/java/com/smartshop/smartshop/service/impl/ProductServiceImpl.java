package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.ConflictException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.PaginationDTO;
import com.smartshop.smartshop.model.dto.ProductDTO;
import com.smartshop.smartshop.model.entity.Product;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.model.mapper.ProductMapper;
import com.smartshop.smartshop.repository.ProductRepository;
import com.smartshop.smartshop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ApiResponse<ProductDTO> create(ProductDTO dto) {
        if (dto == null)
            throw new BadRequestException("Les informations du produit ne peuvent pas être vides");

        String message = "Produit ajouté avec succès!";
        int status = 201;

        Optional<Product> productOp = repository.findByName(dto.getName());
        Product product = mapper.toEntity(dto);

        if (productOp.isPresent()) {
            product = productOp.get();
            product.setStock(product.getStock() + dto.getStock());
            message = "Le produit '" + dto.getName() + "' existe déjà. La quantité a été mise à jour avec succès.";
            status = 200;
        }

        product = repository.save(product);
        return new ApiResponse<>(
                LocalDateTime.now(),
                message,
                status,
                mapper.toDto(product),
                null,
                null
        );
    }

    @Override
    public ApiResponse<ProductDTO> update(UUID uuid, ProductDTO dto) {
        if (dto == null)
            throw new BadRequestException("Les informations du produit ne peuvent pas être vides");

        if (uuid == null)
            throw new BadRequestException("UUID du produit ne peuvent pas être vides");

        Product product = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun clent trouvé avec cet identifiant"));

        boolean updated = false;

        if (!product.getName().equals(dto.getName())) {
            if (repository.findByName(dto.getName()).isPresent())
                throw new ConflictException("Un Produit avec le nom '" + dto.getName() + "' existe déjà.");
            product.setName(dto.getName());
            updated = true;
        }

        if (dto.getStock() != null && !product.getStock().equals(dto.getStock())) {
            product.setStock(dto.getStock());
            updated = false;
        }

        if (!product.getPrcTVA().equals(dto.getPrcTVA())) {
            product.setStock(dto.getStock());
            updated = false;
        }

        if (!product.getPrice().equals(dto.getPrice())) {
            product.setPrice(dto.getPrice());
            updated = false;
        }

        if (updated)
            repository.save(product);

        return new ApiResponse<>(
                LocalDateTime.now(),
                updated
                        ? "Le produit a été mis à jour avec succès!"
                        : "Aucun champ du produit n'a été modifié.",
                200,
                mapper.toDto(product),
                null,
                null
        );
    }

    @Override
    public ApiResponse<List<ProductDTO>> findAll(Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 5 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = repository.findAllProduct(pageable);

        String message = "Aucun produit n'existe dans le système";
        List<ProductDTO> data = List.of();

        if (!products.getContent().isEmpty()) {
            message = "Les produits trouvés avec succès";
            data = products.stream()
                    .map(mapper::toDto)
                    .toList();
        }

        PaginationDTO pagination = new PaginationDTO(
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
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
    public ApiResponse<ProductDTO> find(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("UUID du produit ne peuvent pas être vides");

        Product product = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun clent trouvé avec cet identifiant"));

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Le produit trouvés avec succès",
                200,
                mapper.toDto(product),
                null,
                null
        );
    }

    @Override
    public ApiResponse<ProductDTO> softDelete(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("L'identifiant (UUID) du produit est obligatoire.");

        Product product = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun produit trouvé avec cet identifiant : " + uuid));

        product.setDeletedAt(LocalDateTime.now());
        repository.save(product);

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Produit '" + product.getName() + "' supprimé avec succès (soft delete)",
                200,
                null,
                null,
                null
        );
    }

    @Override
    public ApiResponse<ProductDTO> restore(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("L'identifiant (UUID) du produit est obligatoire.");

        Product product = repository.findDeletedByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun produit soft deleted trouvé avec cet identifiant : " + uuid));

        product.setDeletedAt(null);
        repository.restoreDeleted(uuid);

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Produit '" + product.getName() + "' restauré avec succès",
                200,
                null,
                null,
                null
        );
    }

    @Override
    public ApiResponse<List<ProductDTO>> findAllDeleted(Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 5 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = repository.findAllDeleted(pageable);

        String message = "Aucun produit soft deleted n'existe dans le système";
        List<ProductDTO> data = List.of();

        if (!products.getContent().isEmpty()) {
            message = "Les produits soft deleted trouvés avec succès";
            data = products.stream()
                    .map(mapper::toDto)
                    .toList();
        }

        PaginationDTO pagination = new PaginationDTO(
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isFirst(),
                products.isLast()
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
    public ApiResponse<ProductDTO> findDeleted(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("UUID du produit ne peuvent pas être vides");

        Product product = repository.findDeletedByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun produit soft deleted trouvé avec cet identifiant : " + uuid));

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Le produit soft deleted trouvés avec succès",
                200,
                mapper.toDto(product),
                null,
                null
        );
    }
}
