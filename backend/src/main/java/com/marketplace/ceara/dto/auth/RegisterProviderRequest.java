package com.marketplace.ceara.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Requisição de cadastro para prestador de serviço (US02).
 * Java Record — imutável por definição.
 */
public record RegisterProviderRequest(

        @NotBlank(message = "Nome completo é obrigatório")
        String fullName,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String password,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
        String cpf,

        @NotBlank(message = "Categoria é obrigatória")
        String category,

        String bio,

        Double latitude,

        Double longitude
) {}
