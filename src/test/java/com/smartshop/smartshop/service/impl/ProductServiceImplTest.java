package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.ConflictException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ProductDTO;
import com.smartshop.smartshop.model.entity.Product;
import com.smartshop.smartshop.model.mapper.ProductMapper;
import com.smartshop.smartshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;
    private ProductDTO dto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        product = Product.builder()
                .uuid(UUID.randomUUID())
                .name("Produit A")
                .stock(10)
                .price(BigDecimal.valueOf(100))
                .prcTVA(BigDecimal.valueOf(0.2))
                .build();
        dto = ProductDTO.builder()
                .name("Produit A")
                .stock(10)
                .price(BigDecimal.valueOf(100))
                .prcTVA(BigDecimal.valueOf(0.2))
                .build();
    }

    @Test
    void create_shouldCreateProduct_whenNotExists() {
        when(repository.findByName(dto.getName())).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(product);
        when(repository.save(product)).thenReturn(product);
        when(mapper.toDto(product)).thenReturn(dto);
        var response = service.create(dto);
        assertEquals(201, response.getStatus());
        assertEquals("Produit ajouté avec succès!", response.getMessage());
        verify(repository).save(product);
    }

    @Test
    void create_shouldUpdateStock_whenProductAlreadyExists() {
        Product existing = Product.builder().name("Produit A").stock(5).build();
        when(repository.findByName(dto.getName())).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(dto);
        var response = service.create(dto);
        assertEquals(200, response.getStatus());
        assertEquals(15, existing.getStock());
        verify(repository).save(existing);
    }

    @Test
    void create_shouldThrow_whenDtoIsNull() {
        assertThrows(BadRequestException.class, () -> service.create(null));
    }

    @Test
    void update_shouldUpdateSuccessfully() {
        UUID id = UUID.randomUUID();
        Product updatedProduct = Product.builder()
                .uuid(id)
                .name("Produit B")
                .stock(20)
                .price(BigDecimal.valueOf(150))
                .prcTVA(BigDecimal.valueOf(0.25))
                .build();
        ProductDTO newDto = ProductDTO.builder()
                .name("Produit B")
                .stock(20)
                .price(BigDecimal.valueOf(150))
                .prcTVA(BigDecimal.valueOf(0.25))
                .build();
        when(repository.findByUuid(id)).thenReturn(Optional.of(product));
        when(repository.findByName("Produit B")).thenReturn(Optional.empty());
        when(mapper.toDto(any(Product.class))).thenReturn(newDto);
        var resp = service.update(id, newDto);
        assertEquals(200, resp.getStatus());
        assertEquals("Le produit a été mis à jour avec succès!", resp.getMessage());
        verify(repository).save(any(Product.class));
    }

    @Test
    void update_shouldThrow_whenNameExistsAlready() {
        UUID id = UUID.randomUUID();
        ProductDTO newDto = ProductDTO.builder().name("Produit Existe").build();
        when(repository.findByUuid(id)).thenReturn(Optional.of(product));
        when(repository.findByName("Produit Existe")).thenReturn(Optional.of(new Product()));
        assertThrows(ConflictException.class, () -> service.update(id, newDto));
    }

    @Test
    void update_shouldThrow_whenProductNotExists() {
        assertThrows(NotFoundException.class, () -> service.update(UUID.randomUUID(), new ProductDTO()));
    }

    @Test
    void update_shouldThrow_whenUuidNull() {
        assertThrows(BadRequestException.class, () -> service.update(null, dto));
    }

    @Test
    void update_shouldThrow_whenDtoNull() {
        assertThrows(BadRequestException.class, () -> service.update(UUID.randomUUID(), null));
    }

    @Test
    void update_shouldReturnMessageNoChange() {
        UUID id = UUID.randomUUID();
        when(repository.findByUuid(id)).thenReturn(Optional.of(product));
        when(mapper.toDto(product)).thenReturn(dto);
        var resp = service.update(id, dto);
        assertEquals("Aucun champ du produit n'a été modifié.", resp.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void find_shouldReturnProduct() {
        UUID id = UUID.randomUUID();
        when(repository.findByUuid(id)).thenReturn(Optional.of(product));
        when(mapper.toDto(product)).thenReturn(dto);
        var resp = service.find(id);
        assertEquals(200, resp.getStatus());
        assertEquals("Le produit trouvés avec succès", resp.getMessage());
    }

    @Test
    void find_shouldThrow_whenUuidNull() {
        assertThrows(BadRequestException.class, () -> service.find(null));
    }

    @Test
    void find_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findByUuid(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.find(id));
    }

    @Test
    void findAll_shouldReturnProducts() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
        Page<Product> page = new PageImpl<>(List.of(product));
        when(repository.findAllProduct(pageable)).thenReturn(page);
        when(mapper.toDto(product)).thenReturn(dto);
        var resp = service.findAll(0, 5);
        assertEquals(200, resp.getStatus());
        assertEquals("Les produits trouvés avec succès", resp.getMessage());
        assertEquals(1, resp.getData().size());
    }

    @Test
    void softDelete_shouldDeleteProduct() {
        UUID id = UUID.randomUUID();
        when(repository.findByUuid(id)).thenReturn(Optional.of(product));
        var resp = service.softDelete(id);
        assertEquals(200, resp.getStatus());
        assertNotNull(product.getDeletedAt());
        verify(repository).save(product);
    }

    @Test
    void restore_shouldRestoreProduct() {
        UUID id = UUID.randomUUID();
        Product deleted = Product.builder()
                .uuid(id)
                .name("Deleted Product")
                .deletedAt(LocalDateTime.now())
                .build();
        when(repository.findDeletedByUuid(id)).thenReturn(Optional.of(deleted));
        var resp = service.restore(id);
        assertEquals(200, resp.getStatus());
        verify(repository).restoreDeleted(id);
    }

    @Test
    void findDeleted_shouldReturnDeletedProduct() {
        UUID id = UUID.randomUUID();
        when(repository.findDeletedByUuid(id)).thenReturn(Optional.of(product));
        when(mapper.toDto(product)).thenReturn(dto);
        var resp = service.findDeleted(id);
        assertEquals(200, resp.getStatus());
    }

    @Test
    void findDeleted_shouldThrow_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findDeletedByUuid(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findDeleted(id));
    }

    @Test
    void findAllDeleted_shouldReturnList() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
        Page<Product> page = new PageImpl<>(List.of(product));
        when(repository.findAllDeleted(pageable)).thenReturn(page);
        when(mapper.toDto(product)).thenReturn(dto);
        var resp = service.findAllDeleted(0, 5);
        assertEquals(200, resp.getStatus());
        assertEquals("Les produits soft deleted trouvés avec succès", resp.getMessage());
    }
}
