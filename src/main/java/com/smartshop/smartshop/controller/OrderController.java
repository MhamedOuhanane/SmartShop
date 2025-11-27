package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.OrderDTO;
import com.smartshop.smartshop.model.entity.Order;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.repository.OrderRepository;
import com.smartshop.smartshop.service.interfaces.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping
    public ResponseEntity<?> shows(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        var result = service.findAll(page, size);

        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> show(
            @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.find(uuid);

        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody OrderDTO dto,
            HttpServletRequest req
    ) {
        var result = service.create(dto);

        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping("/{uuid}/{status}")
    public ResponseEntity<?> updateStatus(
            @PathVariable("uuid") UUID uuid,
            @PathVariable("status") OrderStatus status,
            HttpServletRequest req
    ) {
        var result = service.updateStatus(uuid, status);

        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }


}
