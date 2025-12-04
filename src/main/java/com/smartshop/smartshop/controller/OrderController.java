package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.OrderDTO;
import com.smartshop.smartshop.model.enums.OrderStatus;
import com.smartshop.smartshop.service.interfaces.OrderService;
import com.smartshop.smartshop.service.interfaces.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Commandes", description = "API de gestion des commandes (création, consultation, mise à jour du statut, paiements associés).")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;
    private final PaymentService paymentService;

    @Operation(
            summary = "Lister les commandes",
            description = "Récupère toutes les commandes avec pagination, triées par date de création (plus récentes en premier).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès"),
                    @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<?> shows(
            @Parameter(description = "Numéro de la page") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        var result = service.findAll(page, size);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Récupérer une commande",
            description = "Récupère les détails complets d'une commande par son UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Commande trouvée"),
                    @ApiResponse(responseCode = "404", description = "Commande non trouvée", content = @Content),
                    @ApiResponse(responseCode = "400", description = "UUID invalide", content = @Content)
            }
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<?> show(
            @Parameter(description = "UUID de la commande") @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.find(uuid);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Créer une commande",
            description = "Crée une nouvelle commande avec validation du stock. Si le stock est insuffisant, la commande est automatiquement rejetée. Applique les remises selon le niveau de fidélité du client.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
                    @ApiResponse(responseCode = "400", description = "Commande rejetée - stock insuffisant ou données invalides", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Client ou produit non trouvé", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<?> create(
            @Parameter(description = "Données de la commande à créer") @Valid @RequestBody OrderDTO dto,
            HttpServletRequest req
    ) {
        var result = service.create(dto);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Mettre à jour le statut d'une commande",
            description = "Change le statut d'une commande (PENDING → CONFIRMED ou CANCELED). Une commande ne peut être confirmée que si elle est entièrement payée. L'annulation restaure le stock.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Transition de statut invalide ou conditions non remplies", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Commande non trouvée", content = @Content)
            }
    )
    @PutMapping("/{uuid}/{status}")
    public ResponseEntity<?> updateStatus(
            @Parameter(description = "UUID de la commande") @PathVariable("uuid") UUID uuid,
            @Parameter(description = "Nouveau statut (CONFIRMED, CANCELED)") @PathVariable("status") OrderStatus status,
            HttpServletRequest req
    ) {
        var result = service.updateStatus(uuid, status);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Lister les paiements d'une commande",
            description = "Récupère l'historique des paiements associés à une commande spécifique avec pagination.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès"),
                    @ApiResponse(responseCode = "404", description = "Commande non trouvée", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content)
            }
    )
    @GetMapping("/{uuid}/payments")
    public ResponseEntity<?> showsPayment(
            @Parameter(description = "UUID de la commande") @PathVariable("uuid") UUID uuid,
            @Parameter(description = "Numéro de la page") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        var result = paymentService.findOrderPayments(uuid, page, size);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}