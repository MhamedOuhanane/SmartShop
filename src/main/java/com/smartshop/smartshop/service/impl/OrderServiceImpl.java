package com.smartshop.smartshop.service.impl;

import com.smartshop.smartshop.exception.generic.BadRequestException;
import com.smartshop.smartshop.exception.generic.NotFoundException;
import com.smartshop.smartshop.model.dto.*;
import com.smartshop.smartshop.model.entity.*;
import com.smartshop.smartshop.model.enums.CustomerTier;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.model.mapper.OrderMapper;
import com.smartshop.smartshop.repository.ClientRepository;
import com.smartshop.smartshop.repository.OrderRepository;
import com.smartshop.smartshop.repository.ProductRepository;
import com.smartshop.smartshop.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        var products = loadProducts(dto.getOrderItems());

        var validatedStock = validStock(dto.getOrderItems(), products);
        boolean insufficientStock = (boolean) validatedStock.get("insufficientStock");
        OrderStatus status = insufficientStock
                ? OrderStatus.REJECTED
                : OrderStatus.PENDING;

        Order order = mapper.toEntity(dto);
        order.setClient(client);
        order.setStatus(status);

        List<OrderItem> items = buildOrderItem(order, dto.getOrderItems(), products);
        BigDecimal subTotal = calculSubTotal(items);

        order.setOrderItems(items);
        order.setVat(calculateVAT(items));
        order.setSubTotal(subTotal);
        order.setDiscount(calculDiscountPorc(client, subTotal));
        order.setTotal(calculTotal(order));
        order.setDate(LocalDateTime.now());
        order.setRemainingAmount(order.getTotal());

        order = repository.save(order);
        if (status == OrderStatus.PENDING) {
            List<Product> productsUpdate = updateStock(order.getOrderItems(), OrderStatus.PENDING);
            productRepository.saveAll(productsUpdate);
        }

        String message = (String) validatedStock.get("message");

        return new ApiResponse<>(
                LocalDateTime.now(),
                message,
                201,
                mapper.toDto(order),
                null,
                null
        );

    }

    @Override
    public ApiResponse<OrderDTO> updateStatus(UUID uuid, OrderStatus newStatus) {
        if (uuid == null)
            throw new BadRequestException("L'identifiant de la commande est obligatoire.");

        if (newStatus == null)
            throw new BadRequestException("Le statut de la commande est obligatoire.");

        Order order = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucune commande trouvée avec cet identifiant : " + uuid));

        order = handleStatusTransition(order, newStatus);

        order = repository.save(order);
        if (newStatus.equals(OrderStatus.CANCELED)) {
            List<Product> products = updateStock(order.getOrderItems(), newStatus);
            productRepository.saveAll(products);
        } else if (newStatus.equals(OrderStatus.CONFIRMED))
            handleLoyaltyLevelClient(order.getClient());

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Le statut de la commande '" + uuid + "' a été mis à jour avec succès à '" + newStatus.getDescription() + "'.",
                200,
                mapper.toDto(order),
                null,
                null
        );
    }

    @Override
    public ApiResponse<List<OrderDTO>> findAll(Integer page, Integer size) {
        page = page == null ? 0 : page;
        size = size == null ? 5 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> payments = repository.findAll(pageable);

        String message = "Aucun commande n'existe dans le système";
        List<OrderDTO> data = List.of();

        if (!payments.getContent().isEmpty()) {
            message = "Les commandes trouvés avec succès";
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
    public ApiResponse<OrderDTO> find(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("UUID du commande ne peuvent pas être vides");

        Order order = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun commande trouvé avec identifiant " + uuid));

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Le order trouvés avec succès",
                200,
                mapper.toDto(order),
                null,
                null
        );
    }

    @Override
    public ApiResponse<List<OrderDTO>> findClientOrders(UUID uuid, Integer page, Integer size) {
        if (uuid == null)
            throw new BadRequestException("UUID du client ne peuvent pas être vides");

        Client client = clientRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun client trouvé avec identifiant: "+ uuid));

        page = page == null ? 0 : page;
        size = size == null ? 5 : size;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> payments = repository.findByClient(client, pageable);

        String message = "Aucun commande n'existe!";
        List<OrderDTO> data = List.of();

        if (!payments.getContent().isEmpty()) {
            message = "Les commandes trouvés avec succès!";
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
    public ApiResponse<?> findClientStatistics(UUID uuid) {
        if (uuid == null)
            throw new BadRequestException("UUID du client ne peuvent pas être vides");

        Client client = clientRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Aucun client trouvé avec cet identifiant"));

        List<Order> orders = repository.findAllByClient(client);

        int count = orders.size();
        BigDecimal total = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .map(Order::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime first = orders.stream()
                .map(Order::getDate)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime last = orders.stream()
                .map(Order::getDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Map<String, Object> stats = Map.of(
                "totalOrders", count,
                "totalAmount", total,
                "firstOrderDate", first,
                "lastOrderDate", last
        );

        return new ApiResponse<>(
                LocalDateTime.now(),
                "Statistiques des commandes clients " + client.getName(),
                200,
                stats,
                null,
                null
        );
    }

    private Map<UUID, Product> loadProducts(List<OrderItemDTO> itemsDto) {
        List<UUID> uuids = itemsDto.stream()
                .map(OrderItemDTO::getProductUuid)
                .toList();
        List<Product> products = productRepository.findByUuidIn(uuids);

        if (products.size() != uuids.size()) {
            List<UUID> foundUuids = products.stream()
                    .map(Product::getUuid)
                    .toList();

            List<UUID> missingUuids = uuids.stream()
                    .filter(uuid -> !foundUuids.contains(uuid))
                    .toList();

            throw new NotFoundException(
                    "Produits introuvables pour les UUID : " + missingUuids
            );
        }

        return products.stream()
                .collect(Collectors.toMap(Product::getUuid, p -> p));
    }

    private List<OrderItem> buildOrderItem(Order order, List<OrderItemDTO> itemsDto, Map<UUID, Product> products) {
        return itemsDto.stream().map( dto -> {
            Product product = products.get(dto.getProductUuid());
            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalLine(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                    .build();
        }).toList();
    }

    private BigDecimal calculateVAT(List<OrderItem> items) {
        return items.stream()
                .map(i -> i.getUnitPrice()
                        .multiply(i.getProduct().getPrcTVA())
                        .multiply(BigDecimal.valueOf(i.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculSubTotal(List<OrderItem> items) {
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
                CustomerTier.BASIC, new TreeMap<>(Map.of()),
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
        return entry == null ? BigDecimal.ZERO : entry.getValue();
    }

    private BigDecimal bd(double d) {
        return BigDecimal.valueOf(d);
    }

    private List<Product> updateStock(List<OrderItem> items, OrderStatus status) {
        if (items == null || items.isEmpty()) return List.of();
        List<Product> products = new ArrayList<>();
        int factor;

        switch (status) {
            case PENDING -> factor = -1;
            case CANCELED -> factor = 1;
            default -> throw new BadRequestException(
                    "Impossible de mettre à jour le stock pour le statut : " + status
            );
        }
        for (OrderItem item : items) {
            Product product = item.getProduct();
            int newStock = product.getStock() + (factor * item.getQuantity());

            if (newStock < 0)
                throw new BadRequestException(
                        "Stock insuffisant pour le produit : " + product.getName()
                );

            product.setStock(newStock);
            products.add(product);
        }
        return products;
    }

    private void handleLoyaltyLevelClient(Client client) {
        List<Order> orders = repository.findAllByClient(client).stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .toList();

        int orderCount = orders.size();
        BigDecimal totalAmount = orders.stream()
                .map(Order::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CustomerTier newTier = CustomerTier.BASIC;
        if (orderCount >= 20 || totalAmount.compareTo(BigDecimal.valueOf(15_000)) >= 0)
            newTier = CustomerTier.PLATINUM;
        else if (orderCount >= 10 || totalAmount.compareTo(BigDecimal.valueOf(5_000)) >= 0)
            newTier = CustomerTier.GOLD;
        else if (orderCount >= 3 || totalAmount.compareTo(BigDecimal.valueOf(1_000)) >= 0)
            newTier = CustomerTier.SILVER;
        if (!client.getLoyaltyLevel().equals(newTier)) {
            client.setLoyaltyLevel(newTier);
            clientRepository.save(client);
        }
    }

    private Order handleStatusTransition(Order order, OrderStatus newStatus) {
        if (order.getStatus().equals(newStatus))
            throw new BadRequestException("Le statut de ce commande est déjà " + newStatus.getDescription());

        if (newStatus == OrderStatus.REJECTED)
            throw new BadRequestException(
                    "Le statut 'Rejetée' est réservé pour les commandes automatiquement rejetées (ex: stock insuffisant) et ne peut pas être défini manuellement."
            );

        if (order.getStatus() != OrderStatus.PENDING)
            throw new BadRequestException(
                    "Transition non autorisée depuis le statut '" + order.getStatus().getDescription() + "'."
            );

        switch (newStatus) {
            case CONFIRMED -> {
                if (order.getRemainingAmount().compareTo(BigDecimal.ZERO) != 0)
                    throw new BadRequestException(
                            "Impossible de confirmer la commande : le montant restant à payer est de " + order.getRemainingAmount()
                    );

                order.setStatus(newStatus);
            }

            case CANCELED -> {
                if (order.getRemainingAmount().compareTo(order.getTotal()) != 0)
                    throw new BadRequestException(
                            "Impossible d'annuler la commande : au moins un paiement a déjà été effectué."
                    );

                order.setStatus(newStatus);
            }

            case PENDING -> throw new BadRequestException("Transition non autorisée : Le statut ne peut pas être modifié a 'en attente'.");
            default -> throw new BadRequestException("Transition de statut non reconnue");
        }

        return order;
    }

    private Map<String, Object> validStock(List<OrderItemDTO> itemsDto, Map<UUID, Product> products) {
        StringBuilder message = new StringBuilder("Commande rejetée : stock insuffisant.");
        boolean insufficientStock = false;

        for (OrderItemDTO itemDto : itemsDto) {
            Product product = products.get(itemDto.getProductUuid());

            if (product.getStock().compareTo(itemDto.getQuantity()) < 0) {
                insufficientStock = true;
                message.append(String.format(
                        "; Produit: %s | demandé: %d | disponible: %d",
                        product.getName(), itemDto.getQuantity(), product.getStock()));
            }
        }

        return Map.of(
                "message", message.toString(),
                "insufficientStock", insufficientStock
        );
    }
}
