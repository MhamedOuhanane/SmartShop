package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.PaginationDTO;
import com.smartshop.smartshop.model.dto.PaymentDTO;
import com.smartshop.smartshop.model.entity.Order;
import com.smartshop.smartshop.model.entity.Payment;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.model.enums.PaymentStatus;
import com.smartshop.smartshop.model.enums.PaymentType;
import com.smartshop.smartshop.model.mapper.PaymentMapper;
import com.smartshop.smartshop.repository.OrderRepository;
import com.smartshop.smartshop.repository.PaymentRepository;
import com.smartshop.smartshop.service.interfaces.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderRepository orderRepository;

    @Override
    public ApiResponse<PaymentDTO> create(PaymentDTO dto) {
        if (dto == null)
            throw new BadRequestException("Les informations du paiement ne peuvent pas être vides");

        Payment payment = mapper.toEntity(dto);

        Order order = orderRepository.findByUuid(dto.getOrderUuid())
                .orElseThrow(() -> new NotFoundException("Aucun commande trouvé avec cet identifiant"));

        if (!order.getStatus().equals(OrderStatus.PENDING))
            throw new BadRequestException(
                    "Impossible de faire ce paiement : l'état actuel du commande est '"
                            + order.getStatus().getDescription() + "'. Seuls les orders en attente peuvent être payés."
            );

        String msg = "Paiement ajouté avec succès!";
        PaymentStatus status = PaymentStatus.PENDING;

        if (payment.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            msg = String.format("Le montant du paiement (%.2f MAD) ne peut pas dépasser le montant restant de la commande (%.2f MAD)."
                    , payment.getAmount(), order.getRemainingAmount()
            );
            status = PaymentStatus.REJECTED;
        }

        if (payment.getPaymentType() == PaymentType.CASH && payment.getAmount().compareTo(BigDecimal.valueOf(20_000)) > 0) {
            msg = String.format(
                    "Le paiement en espèces est limité à 20 000 MAD par opération (Art. 193 CGI). Montant reçu : %.2f MAD.",
                    payment.getAmount()
            );
            status = PaymentStatus.REJECTED;
        }

        Long count = repository.countByOrderUuid(order.getUuid());

        payment.setPaymentNumber(count.intValue() + 1);
        payment.setOrder(order);
        payment.setStatus(status);
        payment.setPaymentDate(LocalDateTime.now());

        payment = repository.save(payment);

        BigDecimal remaining = order.getRemainingAmount().subtract(dto.getAmount());
        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            updatePayments(order);
        }
        if (status == PaymentStatus.PENDING) {
            order.setRemainingAmount(remaining);
            orderRepository.save(order);
        }

        return new ApiResponse<>(
                LocalDateTime.now(),
                msg,
                status == PaymentStatus.REJECTED ? 400 : 201,
                mapper.toDto(payment),
                null,
                null
        );
    }

    @Override
    public ApiResponse<List<PaymentDTO>> findAll(Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 5 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").ascending());
        Page<Payment> payments = repository.findAll(pageable);

        String message = "Aucun paiement n'existe dans le système";
        List<PaymentDTO> data = List.of();

        if (!payments.getContent().isEmpty()) {
            message = "Les paiement trouvés avec succès";
            data = payments.stream()
                    .map(mapper::toDto)
                    .toList();
        }

        PaginationDTO pagination = new PaginationDTO(
                payments.getNumber(),
                payments.getSize(),
                payments.getTotalElements(),
                payments.getTotalPages(),
                payments.isFirst(),
                payments.isLast()
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
    public ApiResponse<List<PaymentDTO>> findOrderPayments(UUID uuid, Integer page, Integer size) {
        if (uuid == null)
            throw new BadRequestException("UUID du commande ne peuvent pas être vides");

        Order order = orderRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun commande trouvé avec identifiant: " + uuid));

        page = page == null ? 0 : page;
        size = size == null ? 5 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").ascending());
        Page<Payment> payments = repository.findByOrder(order, pageable);

        String message = "Aucun paiement n'existe pour ce commande";
        List<PaymentDTO> data = List.of();

        if (!payments.getContent().isEmpty()) {
            message = "Les paiement de ce commande trouvés avec succès";
            data = payments.stream()
                    .map(mapper::toDto)
                    .toList();
        }

        PaginationDTO pagination = new PaginationDTO(
                payments.getNumber(),
                payments.getSize(),
                payments.getTotalElements(),
                payments.getTotalPages(),
                payments.isFirst(),
                payments.isLast()
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

    private void updatePayments(Order order) {
        if (order == null) return;

        List<Payment> payments = repository.findAllByOrder(order);
        if (payments.isEmpty()) return;

        payments.forEach(payment -> {
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCollectionDate(LocalDateTime.now());
            }
        });

        repository.saveAll(payments);
    }
}
