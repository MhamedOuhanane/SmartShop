package com.smartshop.smartshop.config;

import com.smartshop.smartshop.exception.generic.ForbiddenException;
import com.smartshop.smartshop.exception.generic.UnauthorizedActionException;
import com.smartshop.smartshop.model.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user_uuid") == null)
            throw new UnauthorizedActionException("Vous devez être connecté.");

        UserRole role = (UserRole) session.getAttribute("user_role");
        UUID uuid = (UUID) session.getAttribute("user_uuid");
        String path = request.getRequestURI();
        String methode = request.getMethod();

        if (path.startsWith("/auth/logout") || role.equals(UserRole.ADMIN))
            return true;

        if (role.equals(UserRole.CLIENT)) {
            if (!methode.equals("GET"))
                throw new ForbiddenException("Les clients ne peuvent pas modifier les données.");

            if (path.startsWith("/admins"))
                throw new ForbiddenException("Accès réservé aux administrateurs.");

            if (path.startsWith("/products"))
                return true;

            if (path.startsWith("/client")) {
                UUID urlUuid = extractUuid(path);

                if (urlUuid != null && !urlUuid.equals(uuid))
                    throw new ForbiddenException("Vous ne pouvez accéder qu'à vos propres données.");

                return true;
            }
        }

        throw new ForbiddenException("Accès non autorisé pour un client.");
    }

    private UUID extractUuid(String path) {
        String[] parts = path.split("/");
        for (String p : parts) {
            if (p.matches("^[0-9a-fA-F-]{36}$"))
                return UUID.fromString(p);
        }
        return null;
    }
}
