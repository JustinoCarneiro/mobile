package com.marketplace.ceara.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.ceara.dto.auth.LoginRequest;
import com.marketplace.ceara.dto.auth.RegisterProviderRequest;
import com.marketplace.ceara.model.enums.VerificationStatus;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import com.marketplace.ceara.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração do Módulo de Identidade.
 *
 * Usa Testcontainers para subir um PostgreSQL real e MockMvc para HTTP.
 *
 * Fluxo testado (US02 + BackgroundCheck):
 *   1. Registrar prestador → status PENDING
 *   2. Aguardar até 10s → BackgroundCheckService atualiza para APPROVED
 *   3. Login correto → JWT válido no body
 *   4. Login inválido → 401 Unauthorized
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Identity Module — Testes de Integração")
class IdentityIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired ProviderProfileRepository providerProfileRepository;

    private static final String BASE_URL = "/api/v1/auth";

    // -------------------------------------------------------------------------
    // Fluxo completo: registro → PENDING → APPROVED (BackgroundCheck async)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Registro de prestador deve iniciar como PENDING e mudar para APPROVED após 5s")
    void shouldTransitionFromPendingToApproved() throws Exception {
        var request = new RegisterProviderRequest(
                "Maria Pintora", "maria@teste.com", "senha1234", "98765432100",
                "Pintura", "Especialista em pintura residencial", 0.0, 0.0
        );

        // 1. Registrar prestador
        mockMvc.perform(post(BASE_URL + "/register/provider")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("PROVIDER"));

        // 2. Verificar status inicial = PENDING
        var user = userRepository.findByEmail("maria@teste.com").orElseThrow();
        var profile = providerProfileRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(profile.getVerificationStatus()).isEqualTo(VerificationStatus.PENDING);

        // 3. Aguardar o BackgroundCheckService (5s delay + margem)
        await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var updated = providerProfileRepository.findByUserId(user.getId()).orElseThrow();
                    assertThat(updated.getVerificationStatus()).isEqualTo(VerificationStatus.APPROVED);
                });
    }

    // -------------------------------------------------------------------------
    // Login com credenciais corretas → JWT no response
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Login com credenciais corretas deve retornar JWT válido")
    void shouldReturnJwtOnValidLogin() throws Exception {
        // Registrar cliente primeiro
        var registerRequest = new com.marketplace.ceara.dto.auth.RegisterClientRequest(
                "Carlos Cliente", "carlos@teste.com", "senha1234", "11122233344"
        );
        mockMvc.perform(post(BASE_URL + "/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login
        var loginRequest = new LoginRequest("carlos@teste.com", "senha1234");
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("carlos@teste.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    // -------------------------------------------------------------------------
    // Login com credenciais incorretas → 401 Unauthorized
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Login com senha incorreta deve retornar 401 Unauthorized")
    void shouldReturn401OnInvalidCredentials() throws Exception {
        var loginRequest = new LoginRequest("naoexiste@teste.com", "senhaErrada");

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
