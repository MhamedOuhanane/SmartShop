package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.service.interfaces.ClientService;
import com.smartshop.smartshop.service.interfaces.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;
    private final OrderService orderService;

    @GetMapping("/profile/{uuid}")
    public ResponseEntity<?> show(
            @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.find(uuid);
        result.setPath(req.getRequestURI());
        if (req.getSession(false).getAttribute("user_role").equals(UserRole.CLIENT))
            result.setMessage("Votre information personnelle trouvés avec succès!");

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> show(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        HttpSession session = req.getSession(false);
        UUID uuid = (UUID) session.getAttribute("user_uuid");
        var result = orderService.findClientOrders(uuid, page, size);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping("/orders/statistics")
    public ResponseEntity<?> show(
            HttpServletRequest req
    ) {
        HttpSession session = req.getSession(false);
        UUID uuid = (UUID) session.getAttribute("user_uuid");
        var result = orderService.findClientStatistics(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

}
