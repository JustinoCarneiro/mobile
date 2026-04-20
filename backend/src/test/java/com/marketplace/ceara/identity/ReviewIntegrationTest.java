package com.marketplace.ceara.identity;

import com.marketplace.ceara.model.ProviderProfile;
import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.Role;
import com.marketplace.ceara.model.enums.ServiceRequestStatus;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import com.marketplace.ceara.repository.ReviewRepository;
import com.marketplace.ceara.repository.ServiceRequestRepository;
import com.marketplace.ceara.repository.UserRepository;
import com.marketplace.ceara.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Review — Testes de Integração e Reputação (US08)")
class ReviewIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired ServiceRequestRepository serviceRequestRepository;
    @Autowired ReviewRepository reviewRepository;
    @Autowired ProviderProfileRepository providerProfileRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtTokenService jwtTokenService;

    private User client;
    private User providerUser;
    private String clientToken;

    @BeforeEach
    void setup() {
        reviewRepository.deleteAll();
        serviceRequestRepository.deleteAll();
        providerProfileRepository.deleteAll();
        userRepository.deleteAll();

        // Criar Cliente
        client = new User("Cliente Avaliador", "cliente@review.com", passwordEncoder.encode("123"), "12312312311", Role.CLIENT);
        userRepository.save(client);
        clientToken = "Bearer " + jwtTokenService.generateToken(client);

        // Criar Prestador e seu Perfil
        providerUser = new User("Prestador Nota 10", "prestador@review.com", passwordEncoder.encode("123"), "32132132133", Role.PROVIDER);
        userRepository.save(providerUser);
        
        ProviderProfile profile = new ProviderProfile(providerUser, "LIMPEZA", "Bio", -3.7, -38.5);
        providerProfileRepository.save(profile);
    }

    @Test
    @DisplayName("Deve recalcular a média do prestador após múltiplas avaliações")
    void shouldRecalculateAverageRating() throws Exception {
        // 1. Criar dois serviços CONCLUIDOS
        ServiceRequest sr1 = new ServiceRequest(client, providerUser, "Serviço 1", null);
        sr1.setStatus(ServiceRequestStatus.COMPLETED);
        serviceRequestRepository.save(sr1);

        ServiceRequest sr2 = new ServiceRequest(client, providerUser, "Serviço 2", null);
        sr2.setStatus(ServiceRequestStatus.COMPLETED);
        serviceRequestRepository.save(sr2);

        // 2. Avaliar Serviço 1 com Nota 4
        mockMvc.perform(post("/api/v1/services/" + sr1.getId() + "/reviews")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 4, \"comment\": \"Muito bom\"}"))
                .andExpect(status().isCreated());

        // 3. Avaliar Serviço 2 com Nota 5
        mockMvc.perform(post("/api/v1/services/" + sr2.getId() + "/reviews")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 5, \"comment\": \"Excelente\"}"))
                .andExpect(status().isCreated());

        // 4. Validar média (4 + 5) / 2 = 4.5
        // Como o cálculo é assíncrono, usamos Awaitility
        await().atMost(5, SECONDS).untilAsserted(() -> {
            ProviderProfile updatedProfile = providerProfileRepository.findByUserId(providerUser.getId()).get();
            assertThat(updatedProfile.getRatingAverage()).isEqualByComparingTo(new BigDecimal("4.50"));
        });
    }
}
