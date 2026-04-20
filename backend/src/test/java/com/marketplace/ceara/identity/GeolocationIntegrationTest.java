package com.marketplace.ceara.identity;

import com.marketplace.ceara.model.ProviderProfile;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.Role;
import com.marketplace.ceara.model.enums.VerificationStatus;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import com.marketplace.ceara.repository.UserRepository;
import com.marketplace.ceara.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Motor de Busca — Testes de Integração Geográfica (US03)")
class GeolocationIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired ProviderProfileRepository providerProfileRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtTokenService jwtTokenService;

    private String authToken;
    private static final String CATEGORY = "LIMPEZA";

    @BeforeEach
    void setup() {
        providerProfileRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Criar um usuário para autenticação
        User client = new User("Cidadao Teste", "cliente@teste.com", passwordEncoder.encode("senha123"), "11111111111", Role.CLIENT);
        userRepository.save(client);
        authToken = "Bearer " + jwtTokenService.generateToken(client);

        // 2. Criar Prestadores em localizações conhecidas (Fortaleza)
        
        // Centro (Referência: -3.7275, -38.5275)
        createProvider("Joao Centro", "joao@centro.com", "22222222222", -3.7275, -38.5275);
        
        // Aldeota (~4km de distância)
        createProvider("Maria Aldeota", "maria@aldeota.com", "33333333333", -3.7389, -38.4947);
        
        // Caucaia (~15km de distância)
        createProvider("Jose Caucaia", "jose@caucaia.com", "44444444444", -3.7327, -38.6592);
    }

    private void createProvider(String name, String email, String cpf, Double lat, Double lng) {
        User user = new User(name, email, passwordEncoder.encode("senha123"), cpf, Role.PROVIDER);
        userRepository.save(user);

        ProviderProfile profile = new ProviderProfile(user, CATEGORY, "Bio de teste", lat, lng);
        profile.setVerificationStatus(VerificationStatus.APPROVED);
        providerProfileRepository.save(profile);
    }

    @Test
    @DisplayName("Deve retornar prestadores num raio de 10km ordenados por distância")
    @SuppressWarnings("null")
    void shouldReturnProvidersWithin10km() throws Exception {
        // Buscando do Centro de Fortaleza
        mockMvc.perform(get("/api/v1/providers/nearby")
                        .header("Authorization", authToken)
                        .param("lat", "-3.7275")
                        .param("lng", "-38.5275")
                        .param("category", CATEGORY)
                        .param("radius", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // João (0km) e Maria (4km). José (15km) fica de fora.
                .andExpect(jsonPath("$[0].fullName", is("Joao Centro")))
                .andExpect(jsonPath("$[0].distance", notNullValue()))
                .andExpect(jsonPath("$[1].fullName", is("Maria Aldeota")));
    }

    @Test
    @DisplayName("Deve retornar todos os 3 prestadores se o raio for aumentado para 20km")
    @SuppressWarnings("null")
    void shouldReturnAllProvidersWithin20km() throws Exception {
        mockMvc.perform(get("/api/v1/providers/nearby")
                        .header("Authorization", authToken)
                        .param("lat", "-3.7275")
                        .param("lng", "-38.5275")
                        .param("category", CATEGORY)
                        .param("radius", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Não deve retornar nada para categoria inexistente")
    @SuppressWarnings("null")
    void shouldReturnEmptyForWrongCategory() throws Exception {
        mockMvc.perform(get("/api/v1/providers/nearby")
                        .header("Authorization", authToken)
                        .param("lat", "-3.7275")
                        .param("lng", "-38.5275")
                        .param("category", "JARDINAGEM")
                        .param("radius", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Deve bloquear acesso sem JWT")
    void shouldBlockAccessWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/v1/providers/nearby")
                        .param("lat", "-3.7275")
                        .param("lng", "-38.5275")
                        .param("category", CATEGORY))
                .andExpect(status().isForbidden());
    }
}
