package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.ClientCreateDTO;
import com.smartshop.smartshop.model.dto.ClientDTO;
import com.smartshop.smartshop.service.interfaces.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {
    private final ClientService clientService;

    @PostMapping("/clients")
    public ResponseEntity<?> createClient(
            @Valid @RequestBody ClientCreateDTO dto,
            HttpServletRequest req
    ) {
        var result = clientService.create(dto);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status((int) result.getStatus()).body(result);
    }

    @PutMapping("/clients/{uuid}")
    public ResponseEntity<?> updateClient(
            @PathVariable UUID uuid,
            @Valid @RequestBody ClientDTO dto,
            HttpServletRequest req
    ) {
        var result = clientService.update(uuid, dto);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status((int) result.getStatus()).body(result);
    }



    @GetMapping("/clients")
    public ResponseEntity<?> showsClient(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        var result = clientService.findAll(page, size);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status((int) result.getStatus()).body(result);
    }
}
