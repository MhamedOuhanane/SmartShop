package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.ProductDTO;
import com.smartshop.smartshop.service.interfaces.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

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
            @Valid @RequestBody ProductDTO dto,
            HttpServletRequest req
    ) {
        var result = service.create(dto);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody ProductDTO dto,
            HttpServletRequest req
    ) {
        var result = service.update(uuid, dto);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(
            @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.softDelete(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping("/{uuid}/restore")
    public ResponseEntity<?> restore(
            @PathVariable("uuid") UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.restore(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping("/deleted")
    public ResponseEntity<?> showsDeleted(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        var result = service.findAllDeleted(page, size);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping("/deleted/{uuid}")
    public ResponseEntity<?> showDeleted(
            @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.findDeleted(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
