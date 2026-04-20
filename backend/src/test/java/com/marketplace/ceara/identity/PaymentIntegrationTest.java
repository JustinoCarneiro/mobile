package com.marketplace.ceara.identity;

import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.Transaction;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.Role;
import com.marketplace.ceara.model.enums.ServiceRequestStatus;
import com.marketplace.ceara.model.enums.TransactionStatus;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import com.marketplace.ceara.repository.ReviewRepository;
import com.marketplace.ceara.repository.ServiceRequestRepository;
import com.marketplace.ceara.repository.TransactionRepository;
import com.marketplace.ceara.repository.UserRepository;
import com.marketplace.ceara.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("Payment & Escrow — Testes de Integração e Atomicidade (US05, US06)")
@SuppressWarnings("null")
class PaymentIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired ServiceRequestRepository serviceRequestRepository;
    @Autowired TransactionRepository transactionRepository;
    @Autowired PasswordEncoder passwordEncoder;
    
    @Autowired ReviewRepository reviewRepository;
    @Autowired ProviderProfileRepository providerProfileRepository;
    @MockitoSpyBean ServiceRequestRepository spyServiceRequestRepository;
    @Autowired PaymentService paymentService;

    private ServiceRequest serviceRequest;
    private User client;
    private User provider;

    @BeforeEach
    void setup() {
        reviewRepository.deleteAll();
        transactionRepository.deleteAll();
        serviceRequestRepository.deleteAll();
        providerProfileRepository.deleteAll();
        userRepository.deleteAll();

        client = new User("Cliente Pagador", "cliente@pay.com", passwordEncoder.encode("123"), "11111111111", Role.CLIENT);
        userRepository.save(client);

        provider = new User("Prestador Recebedor", "prestador@pay.com", passwordEncoder.encode("123"), "22222222222", Role.PROVIDER);
        userRepository.save(provider);

        // Criar um serviço ACEITO pronto para pagamento
        serviceRequest = new ServiceRequest(client, provider, "Serviço de Teste", null);
        serviceRequest.setStatus(ServiceRequestStatus.ACCEPTED);
        serviceRequest = serviceRequestRepository.save(serviceRequest);
    }

    @Test
    @DisplayName("Deve completar o fluxo de pagamento via Webhook com sucesso (Escrow Garantido)")
    void shouldCompletePaymentFlow() throws Exception {
        // 1. Iniciar Checkout
        Transaction tx = paymentService.initiateCheckout(serviceRequest.getId(), new BigDecimal("150.00"));
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(tx.getGatewayId()).startsWith("GTW_");

        // 2. Simular Webhook do Gateway
        String payload = String.format("{\"gateway_id\": \"%s\", \"status\": \"PAID\"}", tx.getGatewayId());
        
        mockMvc.perform(post("/api/v1/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        // 3. Validar se os status foram atualizados atomicamente
        Transaction updatedTx = transactionRepository.findById(tx.getId()).get();
        assertThat(updatedTx.getStatus()).isEqualTo(TransactionStatus.PAID_ESCROW);

        ServiceRequest updatedSr = serviceRequestRepository.findById(serviceRequest.getId()).get();
        assertThat(updatedSr.getStatus()).isEqualTo(ServiceRequestStatus.PAYMENT_CONFIRMED);
    }

    @Test
    @DisplayName("Deve garantir atomicidade: Rollback se houver erro no processamento do Webhook")
    void shouldRollbackOnWebhookError() {
        // 1. Iniciar Checkout
        Transaction tx = paymentService.initiateCheckout(serviceRequest.getId(), new BigDecimal("200.00"));

        // 2. Forçar erro no repositório de ServiceRequest durante o save final do webhook
        doThrow(new RuntimeException("Erro Fatal de Banco")).when(spyServiceRequestRepository).save(any());

        // 3. Chamar o serviço diretamente para capturar a exceção
        try {
            paymentService.processPaymentConfirmation(tx.getGatewayId());
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("Erro Fatal de Banco");
        }

        // 4. Validar que NADA mudou no banco (Rollback)
        Transaction rolledBackTx = transactionRepository.findById(tx.getId()).get();
        assertThat(rolledBackTx.getStatus()).isEqualTo(TransactionStatus.PENDING); // Voltou (ou nunca mudou permanentemente) para PENDING

        ServiceRequest rolledBackSr = serviceRequestRepository.findById(serviceRequest.getId()).get();
        assertThat(rolledBackSr.getStatus()).isEqualTo(ServiceRequestStatus.ACCEPTED); // Permanece ACCEPTED
    }
}
