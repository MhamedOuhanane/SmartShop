package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.ApiResponse;
import com.smartshop.smartshop.model.dto.LoginDto;
import com.smartshop.smartshop.model.dto.UserDTO;
import com.smartshop.smartshop.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginDto dto,
            HttpServletRequest req
    ) {
        var result = userService.login(dto, req);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

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
