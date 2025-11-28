package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.OrderDTO;
import com.smartshop.smartshop.model.dto.PaymentDTO;
import com.smartshop.smartshop.model.enums.PaymentStatus;
import com.smartshop.smartshop.service.interfaces.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

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

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody PaymentDTO dto,
            HttpServletRequest req
    ) {
        var result = service.create(dto);

        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
