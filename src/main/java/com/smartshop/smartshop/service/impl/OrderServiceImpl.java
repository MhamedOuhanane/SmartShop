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
import com.smartshop.smartshop.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;


    @Override
    public ApiResponse<OrderDTO> create(OrderDTO dto) {
        if (dto == null)
            throw new BadRequestException("Les informations du commande ne peuvent pas être vides");


        Client client = clientRepository.findByUuid(dto.getClientUuid())
                .orElseThrow(() -> new NotFoundException("Aucun client trouvé avec cet identifiant"));

        Order order = mapper.toEntity(dto);
        order.setClient(client);
        order.setStatus(OrderStatus.PENDING);

        Set<OrderItem> items = buildOrderItem(order, dto.getOrderItems());
        BigDecimal subTotal = calculSubTotal(items);

        order.setOrderItems(items);
        order.setVat(calculateVAT(items));
        order.setSubTotal(subTotal);
        order.setDiscount(calculDiscountPorc(client, subTotal));
        order.setTotal(calculTotal(order));
        order.setDate(LocalDateTime.now());
        order.setRemainingAmount(order.getTotal());

        order = repository.save(order);

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Commande pour le client '" + client.getName() + "' créée avec succès. " +
                        "Nombre d'articles : " + items.size() + ", Montant total : " + order.getTotal() + " MAD.",
                201,
                mapper.toDto(order),
                null,
                null
        );

    }

    @Override
    public ApiResponse<OrderDTO> update(UUID uuid, OrderDTO dto) {
        return null;
    }

    @Override
    public ApiResponse<List<OrderDTO>> findAll(Integer page, Integer size) {
        return null;
    }

    @Override
    public ApiResponse<OrderDTO> find(UUID uuid) {
        return null;
    }

    @Override
    public ApiResponse<List<OrderDTO>> findClientOrders(UUID uuid, Integer page, Integer size) {
        return null;
    }

    @Override
    public ApiResponse<OrderDTO> findClientStatistics(UUID uuid) {
        return null;
    }

    private Set<OrderItem> buildOrderItem(Order order, Set<OrderItemDTO> itemsDto) {
        Set<OrderItem> items = new HashSet<>();

        for (OrderItemDTO itemDto : itemsDto) {
            Product product = productRepository.findByUuid(itemDto.getProductUuid())
                    .orElseThrow(() -> new NotFoundException("Produit non trouvé : " + itemDto.getProductUuid()));

            if (product.getStock().compareTo(itemDto.getQuantity()) < 0)
                throw new BadRequestException(
                        "La quantité demandée (" + itemDto.getQuantity() +
                                ") dépasse le stock disponible (" + product.getStock() + ") pour le produit : "
                                + product.getName()
                );

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalLine(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())))
                    .build();

            items.add(item);
        }
        return items;
    }

    private BigDecimal calculateVAT(Set<OrderItem> items) {
        return items.stream()
                .map(i -> i.getUnitPrice()
                        .multiply(i.getProduct().getPrcTVA())
                        .multiply(BigDecimal.valueOf(i.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculSubTotal(Set<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalLine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculTotal(Order order) {
        BigDecimal discountAmount = order.getSubTotal().multiply(order.getDiscount());
        BigDecimal amountHt = order.getSubTotal().subtract(discountAmount);

        return amountHt.add(order.getVat());
    }

    private BigDecimal calculDiscountPorc(Client client, BigDecimal subTotal) {
        Map<CustomerTier, TreeMap<BigDecimal, BigDecimal>> rules = Map.of(
                CustomerTier.SILVER, new TreeMap<>(Map.of(
                        bd(500), bd(.05)
                )),
                CustomerTier.GOLD, new TreeMap<>(Map.of(
                        bd(500), bd(.05),
                        bd(800), bd(.1)
                )),
                CustomerTier.PLATINUM, new TreeMap<>(Map.of(
                        bd(500), bd(.05),
                        bd(800), bd(.1),
                        bd(1200), bd(.15)
                ))
        );

        var entry = rules.get(client.getLoyaltyLevel()).floorEntry(subTotal);
        return entry == null ? BigDecimal.ZERO : entry.getKey();
    }

    private BigDecimal bd(double d) {
        return BigDecimal.valueOf(d);
    }
}
