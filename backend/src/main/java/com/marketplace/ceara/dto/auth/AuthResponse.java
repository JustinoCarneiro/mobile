package com.marketplace.ceara.dto.auth;

/**
 * Resposta de autenticação contendo o JWT e metadados do usuário.
 */
public record AuthResponse(
        String token,
        String tokenType,
        String email,
        String role
) {
    public static AuthResponse of(String token, String email, String role) {
        return new AuthResponse(token, "Bearer", email, role);
    }
}
