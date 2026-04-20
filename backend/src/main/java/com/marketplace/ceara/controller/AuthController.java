package com.marketplace.ceara.controller;

import com.marketplace.ceara.dto.auth.*;
import com.marketplace.ceara.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticação.
 *
 * Rotas (todas em /api/v1/auth/**):
 *   POST /register/client   → US01 — cadastro de cliente
 *   POST /register/provider → US02 — cadastro de prestador
 *   POST /login             → autenticação e emissão de JWT
 *
 * Acesso anônimo permitido — configurado em SecurityConfig.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/client")
    public ResponseEntity<AuthResponse> registerClient(
            @Valid @RequestBody RegisterClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerClient(request));
    }

    @PostMapping("/register/provider")
    public ResponseEntity<AuthResponse> registerProvider(
            @Valid @RequestBody RegisterProviderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerProvider(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
