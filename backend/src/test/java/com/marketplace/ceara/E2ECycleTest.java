package com.marketplace.ceara;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.ceara.dto.auth.AuthResponse;
import com.marketplace.ceara.dto.auth.LoginRequest;
import com.marketplace.ceara.dto.auth.RegisterClientRequest;
import com.marketplace.ceara.dto.auth.RegisterProviderRequest;
import com.marketplace.ceara.model.ProviderProfile;
import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.Transaction;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.ServiceRequestStatus;
import com.marketplace.ceara.model.enums.TransactionStatus;
import com.marketplace.ceara.model.enums.VerificationStatus;
import com.marketplace.ceara.repository.*;
import com.marketplace.ceara.service.PaymentService;
import com.marketplace.ceara.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("E2E — Ciclo de Vida Completo do Serviço (20_teste_ponta_a_ponta.md)")
class E2ECycleTest {

    private static final Logger log = LoggerFactory.getLogger(E2ECycleTest.class);

    @MockitoBean 
    private S3Client s3Client;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProviderProfileRepository providerProfileRepository;
    @Autowired private ServiceRequestRepository serviceRequestRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ReviewRepository reviewRepository;

    @Autowired private ServiceRequestService serviceRequestService;
    @Autowired private PaymentService paymentService;

    @BeforeEach
    void setup() {
        reviewRepository.deleteAll();
        transactionRepository.deleteAll();
        serviceRequestRepository.deleteAll();
        providerProfileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve simular o ciclo completo do serviço")
    @SuppressWarnings("null")
    void fullServiceLifecycle() throws Exception {
        long startTime = System.currentTimeMillis();
        log.info(">>> [QA AUDIT] INICIANDO TESTE E2E");

        // 1. Registro
        RegisterClientRequest regClient = new RegisterClientRequest("João QA", "joao.qa@e2e.com", "senha123", "12345678901");
        mockMvc.perform(post("/api/v1/auth/register/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regClient)))
                .andExpect(status().isCreated());

        RegisterProviderRequest regProvider = new RegisterProviderRequest(
                "Maria QA", "maria.qa@e2e.com", "senha123", "98765432100",
                "LIMPEZA", "Bio", 0.0, 0.0
        );
        mockMvc.perform(post("/api/v1/auth/register/provider")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regProvider)))
                .andExpect(status().isCreated());

        userRepository.findByEmail("joao.qa@e2e.com").orElseThrow(); // Apenas garante que existe
        User provider = userRepository.findByEmail("maria.qa@e2e.com").orElseThrow();

        // Background Check
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ProviderProfile profile = providerProfileRepository.findByUserId(provider.getId()).orElseThrow();
            assertThat(profile.getVerificationStatus()).isEqualTo(VerificationStatus.APPROVED);
        });

        // 2. Chamado
        String clientToken = obtainToken("joao.qa@e2e.com", "senha123");
        mockMvc.perform(multipart("/api/v1/services/requests")
                .param("description", "Limpeza")
                .param("providerId", provider.getId().toString())
                .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isCreated());

        List<ServiceRequest> requests = serviceRequestRepository.findAll();
        assertThat(requests).isNotEmpty();
        ServiceRequest sr = requests.get(0);

        // 3. Transição de Estados
        serviceRequestService.updateStatus(sr.getId(), ServiceRequestStatus.ACCEPTED);
        
        // 4. Pagamento
        paymentService.initiateCheckout(sr.getId(), new BigDecimal("350.00"));
        Transaction tx = transactionRepository.findByServiceRequestId(sr.getId()).orElseThrow();
        paymentService.processPaymentConfirmation(tx.getGatewayId());
        
        // 5. Conclusão
        serviceRequestService.updateStatus(sr.getId(), ServiceRequestStatus.COMPLETED);
        
        // 6. Liberação
        paymentService.releaseFunds(sr.getId());
        Transaction releasedTx = transactionRepository.findById(tx.getId()).orElseThrow();
        assertThat(releasedTx.getStatus()).isEqualTo(TransactionStatus.RELEASED);

        // 7. Avaliação
        mockMvc.perform(post("/api/v1/services/" + sr.getId() + "/reviews")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\": 5, \"comment\": \"Top\"}"))
                .andExpect(status().isCreated());

        // Verificação Final
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            ProviderProfile updatedProfile = providerProfileRepository.findByUserId(provider.getId()).orElseThrow();
            assertThat(updatedProfile.getRatingAverage()).isEqualByComparingTo(new BigDecimal("5.00"));
        });

        long duration = System.currentTimeMillis() - startTime;
        log.info(">>> TESTE E2E OK - Tempo: {}ms", duration);
    }

    @SuppressWarnings("null")
    private String obtainToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        AuthResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
        return response.token();
    }
}