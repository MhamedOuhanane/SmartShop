package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.ClientCreateDTO;
import com.smartshop.smartshop.model.dto.ClientDTO;
import com.smartshop.smartshop.service.interfaces.ClientService;
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

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Administration",
        description = "API pour la gestion des clients (création, mise à jour, listing)."
)
@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final ClientService clientService;

    @Operation(
            summary = "Créer un client",
            description = "Ajoute un nouveau client au système.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Client créé avec succès"),
                    @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
            }
    )
    @PostMapping("/clients")
    public ResponseEntity<?> createClient(
            @Parameter(description = "Données du client à créer")
            @Valid @RequestBody ClientCreateDTO dto,
            HttpServletRequest req
    ) {
        var result = clientService.create(dto);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Mettre à jour un client",
            description = "Met à jour les informations d’un client existant via son UUID.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Client mis à jour avec succès"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client non trouvé", content = @Content)
            }
    )
    @PutMapping("/clients/{uuid}")
    public ResponseEntity<?> updateClient(
            @Parameter(description = "UUID du client à mettre à jour")
            @PathVariable UUID uuid,

            @Parameter(description = "Données mises à jour du client")
            @Valid @RequestBody ClientDTO dto,

            HttpServletRequest req
    ) {
        var result = clientService.update(uuid, dto);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Lister les clients",
            description = "Récupère tous les clients avec pagination.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Paramètres invalides", content = @Content)
            }
    )
    @GetMapping("/clients")
    public ResponseEntity<?> showsClient(
            @Parameter(description = "Numéro de page")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Taille de la page")
            @RequestParam(defaultValue = "5") Integer size,

            HttpServletRequest req
    ) {
        var result = clientService.findAll(page, size);
        result.setPath(req.getRequestURI());
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
