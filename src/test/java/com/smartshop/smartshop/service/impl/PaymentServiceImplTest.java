package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.PaymentDTO;
import com.smartshop.smartshop.model.entity.Order;
import com.smartshop.smartshop.model.entity.Payment;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.model.enums.PaymentStatus;
import com.smartshop.smartshop.model.enums.PaymentType;
import com.smartshop.smartshop.model.mapper.PaymentMapper;
import com.smartshop.smartshop.repository.OrderRepository;
import com.smartshop.smartshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository repository;
    @Mock private PaymentMapper mapper;
    @Mock private OrderRepository orderRepository;
    @InjectMocks private PaymentServiceImpl service;

    private Order order;
    private Payment payment;
    private PaymentDTO dto;

    @BeforeEach
    void init() {
        order = Order.builder()
                .uuid(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .remainingAmount(BigDecimal.valueOf(300))
                .build();

        payment = Payment.builder()
                .paymentNumber(1)
                .amount(BigDecimal.valueOf(100))
                .paymentType(PaymentType.CASH)
                .status(PaymentStatus.PENDING)
                .paymentDate(LocalDateTime.now())
                .build();

        dto = PaymentDTO.builder()
                .amount(BigDecimal.valueOf(100))
                .paymentType(PaymentType.CASH)
                .orderUuid(order.getUuid())
                .build();
    }

    @Test
    void create_shouldThrow_whenDtoNull() {
        assertThrows(BadRequestException.class, () -> service.create(null));
    }

    @Test
    void create_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findByUuid(dto.getOrderUuid())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.create(dto));
    }

    @Test
    void create_shouldThrow_whenOrderNotPending() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findByUuid(order.getUuid())).thenReturn(Optional.of(order));
        assertThrows(BadRequestException.class, () -> service.create(dto));
    }

    @Test
    void create_shouldReject_whenAmountGreaterThanRemaining() {
        dto.setAmount(BigDecimal.valueOf(150));
        order.setRemainingAmount(BigDecimal.valueOf(100));

        Payment mapped = Payment.builder()
                .amount(dto.getAmount())
                .paymentType(PaymentType.CASH)
                .build();

        when(orderRepository.findByUuid(dto.getOrderUuid())).thenReturn(Optional.of(order));
        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(repository.countByOrderUuid(order.getUuid())).thenReturn(0L);
        when(repository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            return p;
        });
        when(mapper.toDto(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            PaymentDTO out = new PaymentDTO();
            out.setAmount(p.getAmount());
            out.setPaymentType(p.getPaymentType());
            out.setStatus(p.getStatus());
            return out;
        });

        ApiResponse<PaymentDTO> resp = service.create(dto);

        assertEquals(400, resp.getStatus());
        assertEquals(PaymentStatus.REJECTED, resp.getData().getStatus());
    }

    @Test
    void create_shouldReject_whenCashExceedsLimit() {
        dto.setAmount(BigDecimal.valueOf(25000));
        dto.setPaymentType(PaymentType.CASH);
        order.setRemainingAmount(BigDecimal.valueOf(30000));

        Payment mapped = Payment.builder()
                .amount(dto.getAmount())
                .paymentType(PaymentType.CASH)
                .build();

        when(orderRepository.findByUuid(dto.getOrderUuid())).thenReturn(Optional.of(order));
        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(repository.countByOrderUuid(order.getUuid())).thenReturn(0L);
        when(repository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDto(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            PaymentDTO out = new PaymentDTO();
            out.setAmount(p.getAmount());
            out.setPaymentType(p.getPaymentType());
            out.setStatus(p.getStatus());
            return out;
        });

        ApiResponse<PaymentDTO> resp = service.create(dto);

        assertEquals(400, resp.getStatus());
        assertEquals(PaymentStatus.REJECTED, resp.getData().getStatus());
    }

    @Test
    void create_shouldCreatePayment_andUpdateOrderRemaining() {
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setPaymentType(PaymentType.CASH);
        order.setRemainingAmount(BigDecimal.valueOf(300));

        Payment mapped = Payment.builder()
                .amount(dto.getAmount())
                .paymentType(dto.getPaymentType())
                .build();

        when(orderRepository.findByUuid(dto.getOrderUuid())).thenReturn(Optional.of(order));
        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(repository.countByOrderUuid(order.getUuid())).thenReturn(1L);
        when(repository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDto(any(Payment.class))).thenReturn(new PaymentDTO());

        ApiResponse<PaymentDTO> resp = service.create(dto);

        assertEquals(201, resp.getStatus());
        assertEquals(BigDecimal.valueOf(200), order.getRemainingAmount());
        verify(orderRepository).save(order);
    }

    @Test
    void create_shouldCompleteAllPayments_whenRemainingBecomesZero() {
        dto.setAmount(BigDecimal.valueOf(300));
        dto.setPaymentType(PaymentType.CASH);
        order.setRemainingAmount(BigDecimal.valueOf(300));

        Payment mapped = Payment.builder()
                .amount(dto.getAmount())
                .paymentType(dto.getPaymentType())
                .status(PaymentStatus.PENDING)
                .build();

        Payment pending = Payment.builder()
                .status(PaymentStatus.PENDING)
                .build();

        when(orderRepository.findByUuid(dto.getOrderUuid())).thenReturn(Optional.of(order));
        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(repository.countByOrderUuid(order.getUuid())).thenReturn(0L);
        when(repository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            return p;
        });
        when(repository.findAllByOrder(order)).thenReturn(List.of(pending));
        when(mapper.toDto(any(Payment.class))).thenReturn(new PaymentDTO());

        ApiResponse<PaymentDTO> resp = service.create(dto);

        assertEquals(201, resp.getStatus());
        assertEquals(PaymentStatus.COMPLETED, pending.getStatus());
        assertNotNull(pending.getCollectionDate());
        verify(repository).saveAll(anyList());
    }

    @Test
    void findAll_shouldReturnList() {
        Page<Payment> page = new PageImpl<>(List.of(payment));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(payment)).thenReturn(new PaymentDTO());

        ApiResponse<List<PaymentDTO>> resp = service.findAll(0, 5);

        assertEquals(200, resp.getStatus());
        assertEquals(1, resp.getData().size());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoPayments() {
        Page<Payment> page = new PageImpl<>(List.of());
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        ApiResponse<List<PaymentDTO>> resp = service.findAll(0, 5);

        assertEquals(200, resp.getStatus());
        assertTrue(resp.getData().isEmpty());
    }

    @Test
    void findOrderPayments_shouldThrow_whenUuidNull() {
        assertThrows(BadRequestException.class, () -> service.findOrderPayments(null, 0, 5));
    }

    @Test
    void findOrderPayments_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findByUuid(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findOrderPayments(UUID.randomUUID(), 0, 5));
    }

    @Test
    void findOrderPayments_shouldReturnPayments() {
        Page<Payment> page = new PageImpl<>(List.of(payment));
        when(orderRepository.findByUuid(order.getUuid())).thenReturn(Optional.of(order));
        when(repository.findByOrder(eq(order), any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(payment)).thenReturn(new PaymentDTO());

        ApiResponse<List<PaymentDTO>> resp = service.findOrderPayments(order.getUuid(), 0, 5);

        assertEquals(200, resp.getStatus());
        assertEquals(1, resp.getData().size());
    }
}
