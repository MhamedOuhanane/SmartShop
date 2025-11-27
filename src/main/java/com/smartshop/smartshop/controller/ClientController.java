package com.smartshop.smartshop.controller;

import com.smartshop.smartshop.model.dto.ClientDTO;
import com.smartshop.smartshop.model.enums.UserRole;
import com.smartshop.smartshop.service.interfaces.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;

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

}
