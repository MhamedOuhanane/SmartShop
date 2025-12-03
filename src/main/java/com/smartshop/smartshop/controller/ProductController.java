package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.ProductDTO;
import com.smartshop.smartshop.service.interfaces.ProductService;
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

@Tag(name = "Produits", description = "API de gestion des produits (création, mise à jour, suppression, restauration, recherche).")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @Operation(
            summary = "Lister les produits",
            description = "Récupère tous les produits actifs avec pagination.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès"),
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
            summary = "Récupérer un produit",
            description = "Récupère les détails d'un produit spécifique par son UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produit trouvé"),
                    @ApiResponse(responseCode = "404", description = "Produit non trouvé", content = @Content),
                    @ApiResponse(responseCode = "400", description = "UUID invalide", content = @Content)
            }
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<?> show(
            @Parameter(description = "UUID du produit") @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.find(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Créer un produit",
            description = "Ajoute un nouveau produit ou met à jour le stock si le produit existe déjà.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
                    @ApiResponse(responseCode = "200", description = "Stock du produit existant mis à jour"),
                    @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<?> create(
            @Parameter(description = "Données du produit à créer") @Valid @RequestBody ProductDTO dto,
            HttpServletRequest req
    ) {
        var result = service.create(dto);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Mettre à jour un produit",
            description = "Met à jour les informations d'un produit existant (nom, prix, stock, TVA).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Produit non trouvé", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflit - nom déjà utilisé", content = @Content)
            }
    )
    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(
            @Parameter(description = "UUID du produit") @PathVariable UUID uuid,
            @Parameter(description = "Nouvelles données du produit") @Valid @RequestBody ProductDTO dto,
            HttpServletRequest req
    ) {
        var result = service.update(uuid, dto);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Supprimer un produit (soft delete)",
            description = "Marque un produit comme supprimé sans le retirer de la base de données.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produit supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Produit non trouvé", content = @Content),
                    @ApiResponse(responseCode = "400", description = "UUID invalide", content = @Content)
            }
    )
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(
            @Parameter(description = "UUID du produit à supprimer") @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.softDelete(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Restaurer un produit",
            description = "Restaure un produit précédemment supprimé (soft delete).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produit restauré avec succès"),
                    @ApiResponse(responseCode = "404", description = "Produit supprimé non trouvé", content = @Content),
                    @ApiResponse(responseCode = "400", description = "UUID invalide", content = @Content)
            }
    )
    @PutMapping("/{uuid}/restore")
    public ResponseEntity<?> restore(
            @Parameter(description = "UUID du produit à restaurer") @PathVariable("uuid") UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.restore(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Lister les produits supprimés",
            description = "Récupère tous les produits marqués comme supprimés avec pagination.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des produits supprimés récupérée"),
                    @ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content)
            }
    )
    @GetMapping("/deleted")
    public ResponseEntity<?> showsDeleted(
            @Parameter(description = "Numéro de la page") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest req
    ) {
        var result = service.findAllDeleted(page, size);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Récupérer un produit supprimé",
            description = "Récupère les détails d'un produit spécifique marqué comme supprimé.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produit supprimé trouvé"),
                    @ApiResponse(responseCode = "404", description = "Produit supprimé non trouvé", content = @Content),
                    @ApiResponse(responseCode = "400", description = "UUID invalide", content = @Content)
            }
    )
    @GetMapping("/deleted/{uuid}")
    public ResponseEntity<?> showDeleted(
            @Parameter(description = "UUID du produit supprimé") @PathVariable UUID uuid,
            HttpServletRequest req
    ) {
        var result = service.findDeleted(uuid);
        result.setPath(req.getRequestURI());

        return ResponseEntity.status(result.getStatus()).body(result);
    }
}