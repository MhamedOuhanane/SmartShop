package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.PaymentDTO;
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

@Tag(name = "Paiements", description = "API de gestion des paiements (création, consultation).")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;

    @Operation(
            summary = "Lister les paiements",
            description = "Récupère tous les paiements avec pagination.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès"),
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
            summary = "Créer un paiement",
            description = "Enregistre un nouveau paiement pour une commande. Vérifie les limites de paiement en espèces (20 000 MAD) et le montant restant de la commande.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Paiement créé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Paiement rejeté - montant invalide ou limite dépassée", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Commande non trouvée", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<?> create(
            @Parameter(description = "Données du paiement à créer") @Valid @RequestBody PaymentDTO dto,
            HttpServletRequest req
    ) {
        var result = service.create(dto);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}