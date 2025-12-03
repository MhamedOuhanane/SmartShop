package com.smartshop.smartshop.service.impl;


import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.OrderDTO;
import com.smartshop.smartshop.model.dto.OrderItemDTO;
import com.smartshop.smartshop.model.entity.Client;
import com.smartshop.smartshop.model.entity.Order;
import com.smartshop.smartshop.model.entity.OrderItem;
import com.smartshop.smartshop.model.entity.Product;
import com.smartshop.smartshop.model.enums.CustomerTier;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.model.mapper.OrderMapper;
import com.smartshop.smartshop.repository.ClientRepository;
import com.smartshop.smartshop.repository.OrderRepository;
import com.smartshop.smartshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository repository;
    @Mock private OrderMapper mapper;
    @Mock private ClientRepository clientRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private OrderServiceImpl service;

    private Client client;
    private Order order;
    private Product product;
    private OrderDTO dto;
    private OrderItemDTO itemDto;

    @BeforeEach
    void init() {
        client = Client.builder()
                .uuid(UUID.randomUUID())
                .name("Client A")
                .loyaltyLevel(CustomerTier.BASIC)
                .build();

        product = Product.builder()
                .uuid(UUID.randomUUID())
                .name("P1")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .prcTVA(BigDecimal.valueOf(0.2))
                .build();

        itemDto = OrderItemDTO.builder()
                .productUuid(product.getUuid())
                .quantity(2)
                .build();

        dto = OrderDTO.builder()
                .clientUuid(client.getUuid())
                .orderItems(List.of(itemDto))
                .build();

        order = Order.builder()
                .uuid(UUID.randomUUID())
                .date(LocalDateTime.of(2025, 11, 28, 18, 40, 37))
                .client(client)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .subTotal(BigDecimal.valueOf(200))
                .vat(BigDecimal.valueOf(40))
                .discount(BigDecimal.ZERO)
                .total(BigDecimal.valueOf(240))
                .remainingAmount(BigDecimal.valueOf(240))
                .build();
    }

    @Test
    void create_shouldCreatePendingOrder_whenStockSufficient() {
        when(clientRepository.findByUuid(dto.getClientUuid())).thenReturn(Optional.of(client));
        when(productRepository.findByUuidIn(any())).thenReturn(List.of(product));
        when(mapper.toEntity(dto)).thenReturn(order);
        when(repository.save(order)).thenReturn(order);
        when(mapper.toDto(order)).thenReturn(dto);

        ApiResponse<OrderDTO> resp = service.create(dto);

        assertEquals(201, resp.getStatus());
        verify(productRepository).saveAll(any());
    }

    @Test
    void create_shouldReject_whenStockInsufficient() {
        product.setStock(1);

        when(clientRepository.findByUuid(dto.getClientUuid())).thenReturn(Optional.of(client));
        when(productRepository.findByUuidIn(any())).thenReturn(List.of(product));
        when(mapper.toEntity(dto)).thenReturn(order);
        when(repository.save(order)).thenReturn(order);

        ApiResponse<OrderDTO> resp = service.create(dto);

        assertEquals(400, resp.getStatus());
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @Test
    void create_shouldThrow_whenDtoNull() {
        assertThrows(BadRequestException.class, () -> service.create(null));
    }

    @Test
    void create_shouldThrow_whenClientNotFound() {
        when(clientRepository.findByUuid(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.create(dto));
    }

    @Test
    void updateStatus_shouldConfirmOrder() {
        order.setRemainingAmount(BigDecimal.ZERO);
        when(repository.findByUuid(order.getUuid())).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);
        when(mapper.toDto(order)).thenReturn(dto);

        ApiResponse<OrderDTO> resp = service.updateStatus(order.getUuid(), OrderStatus.CONFIRMED);

        assertEquals(200, resp.getStatus());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void updateStatus_shouldRestoreStock_whenCanceled() {
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(3)
                .build();

        order.setOrderItems(List.of(item));
        order.setRemainingAmount(order.getTotal());

        when(repository.findByUuid(order.getUuid())).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        ApiResponse<OrderDTO> resp = service.updateStatus(order.getUuid(), OrderStatus.CANCELED);

        assertEquals(200, resp.getStatus());
        assertEquals(13, product.getStock());
        verify(productRepository).saveAll(any());
    }

    @Test
    void updateStatus_shouldThrow_whenInvalidTransition() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(repository.findByUuid(order.getUuid())).thenReturn(Optional.of(order));
        assertThrows(BadRequestException.class, () -> service.updateStatus(order.getUuid(), OrderStatus.CANCELED));
    }

    @Test
    void find_shouldReturnOrder() {
        when(repository.findByUuid(order.getUuid())).thenReturn(Optional.of(order));
        when(mapper.toDto(order)).thenReturn(dto);

        var resp = service.find(order.getUuid());

        assertEquals(200, resp.getStatus());
    }

    @Test
    void find_shouldThrow_whenNotFound() {
        when(repository.findByUuid(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.find(UUID.randomUUID()));
    }

    @Test
    void findAll_shouldReturnList() {
        Page<Order> page = new PageImpl<>(List.of(order));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(order)).thenReturn(dto);

        var resp = service.findAll(0, 5);

        assertEquals(200, resp.getStatus());
        assertEquals(1, resp.getData().size());
    }

    @Test
    void findClientOrders_shouldReturnOrders() {
        Page<Order> page = new PageImpl<>(List.of(order));
        when(clientRepository.findByUuid(client.getUuid())).thenReturn(Optional.of(client));
        when(repository.findByClient(eq(client), any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(order)).thenReturn(dto);

        var resp = service.findClientOrders(client.getUuid(), 0, 5);

        assertEquals(200, resp.getStatus());
    }

    @Test
    void findClientStatistics_shouldReturnStats() {
        when(clientRepository.findByUuid(client.getUuid())).thenReturn(Optional.of(client));
        when(repository.findAllByClient(client)).thenReturn(List.of(order));

        var resp = service.findClientStatistics(client.getUuid());

        assertEquals(200, resp.getStatus());
        assertInstanceOf(Map.class, resp.getData());
    }

    @Test
    void findClientStatistics_shouldThrow_whenClientNotFound() {
        when(clientRepository.findByUuid(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findClientStatistics(UUID.randomUUID()));
    }
}
