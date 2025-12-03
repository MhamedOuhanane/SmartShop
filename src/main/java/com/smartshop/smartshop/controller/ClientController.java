package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.service.interfaces.ClientService;
import com.smartshop.smartshop.service.interfaces.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Clients", description = "API espace client (profil, commandes, statistiques).")
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;
    private final OrderService orderService;

    @Operation(
            summary = "Consulter le profil d'un client",
            description = "Récupère les informations personnelles d'un client par son UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès"),
                    @ApiResponse(responseCode = "404", description = "Client non trouvé", content = @Content),
                    @ApiResponse(responseCode = "400", description = "UUID invalide", content = @Content)
            }
    )
    @GetMapping("/profile/{uuid}")
    public ResponseEntity<?> show(
            @Parameter(description = "UUID du client") @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.find(uuid);
        result.setPath(req.getRequestURI());
        if (req.getSession(false).getAttribute("user_role").equals(UserRole.CLIENT))
            result.setMessage("Votre information personnelle trouvés avec succès!");

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Consulter les commandes d'un client",
            description = "Récupère toutes les commandes du client connecté avec pagination.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée"),
                    @ApiResponse(responseCode = "401", description = "Client non authentifié", content = @Content)
            }
    )
    @GetMapping("/orders")
    public ResponseEntity<?> show(
            @Parameter(description = "Numéro de la page") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        HttpSession session = req.getSession(false);
        UUID uuid = (UUID) session.getAttribute("user_uuid");
        var result = orderService.findClientOrders(uuid, page, size);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Consulter les statistiques d'un client",
            description = "Récupère les statistiques des commandes du client connecté (nombre total, montant total, dates première/dernière commande).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
                    @ApiResponse(responseCode = "401", description = "Client non authentifié", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Client non trouvé", content = @Content)
            }
    )
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