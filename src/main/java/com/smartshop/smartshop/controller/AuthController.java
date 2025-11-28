package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.LoginDto;
import com.smartshop.smartshop.model.dto.UserDTO;
import com.smartshop.smartshop.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "Opérations d'authentification et de gestion de session")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @Operation(
            summary = "Connexion d'utilisateur",
            description = "Authentifie un utilisateur et crée une session (JSESSIONID). Le cookie de session est envoyé dans l'en-tête 'Set-Cookie'.",
            operationId = "loginUser"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Connexion réussie",
            content = @Content(schema = @Schema(implementation = UserDTO.class))
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nom d'utilisateur et mot de passe pour la connexion",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginDto.class),
                            examples = @ExampleObject(
                                    name = "Exemple de connexion",
                                    value = "{\"username\": \"testuser\", \"password\": \"MonMotDePasse123\"}"
                            )
                    )
            )
            @Valid @RequestBody LoginDto dto,
            HttpServletRequest req
    ) {
        var result = userService.login(dto);
        result.setPath(req.getRequestURI());
        UserDTO user = result.getData();

        HttpSession session = req.getSession(true);
        session.setAttribute("user_uuid", user.getUuid());
        session.setAttribute("user_role", user.getRole());

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @Operation(
            summary = "Déconnexion d'utilisateur",
            description = "Invalide la session utilisateur actuelle (JSESSIONID).",
            operationId = "logoutUser"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Déconnecté avec succès",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Succès",
                            value = "{\"message\": \"Déconnecté avec succès\", \"status\": 200}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Aucune session trouvée",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Échec",
                            value = "{\"message\": \"Aucune session trouvée\", \"status\": 400}"
                    )
            )
    )
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
            return ResponseEntity.ok(Map.of(
                    "message", "Déconnecté avec succès",
                    "status", 200
            ));
        }

        return ResponseEntity.status(400).body(Map.of(
                "message", "Aucune session trouvée",
                "status", 400
        ));
    }
}
