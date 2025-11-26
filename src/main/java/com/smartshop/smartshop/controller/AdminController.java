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

    @GetMapping("/clients")
    public ResponseEntity<?> showsClient(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        var result = clientService.findAll(page, size);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status((int) result.getStatus()).body(result);
    }
}
