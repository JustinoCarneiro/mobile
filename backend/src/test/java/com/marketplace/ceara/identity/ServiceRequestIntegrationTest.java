package com.marketplace.ceara.identity;

import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.Role;
import com.marketplace.ceara.model.enums.ServiceRequestStatus;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Service Request — Testes de Integração Multimídia (US04)")
class ServiceRequestIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired ServiceRequestRepository serviceRequestRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JwtTokenService jwtTokenService;
    @Autowired S3Client s3Client;

    private User client;
    private User provider;
    private String clientToken;

    @BeforeEach
    void setup() {
        serviceRequestRepository.deleteAll();
        userRepository.deleteAll();

        // Criar Cliente
        client = new User("Cliente das Neves", "cliente@us04.com", passwordEncoder.encode("senha123"), "11122233344", Role.CLIENT);
        userRepository.save(client);
        clientToken = "Bearer " + jwtTokenService.generateToken(client);

        // Criar Prestador
        provider = new User("Prestador de Java", "prestador@us04.com", passwordEncoder.encode("senha123"), "55566677788", Role.PROVIDER);
        userRepository.save(provider);
    }

    @Test
    @DisplayName("Deve criar um chamado com imagem e validar persistência e storage")
    void shouldCreateServiceRequestWithFile() throws Exception {
        // 1. Preparar o arquivo mockado
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "problema_cano.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "conteúdo da imagem de teste".getBytes()
        );

        // 2. Executar POST multipart
        var result = mockMvc.perform(multipart("/api/v1/services/requests")
                        .file(file)
                        .param("description", "Vazamento no banheiro social")
                        .param("providerId", provider.getId().toString())
                        .header("Authorization", clientToken))
                .andExpect(status().isCreated())
                .andReturn();

        // 3. Validar no Banco de Dados
        var requests = serviceRequestRepository.findAll();
        assertThat(requests).hasSize(1);
        var request = requests.get(0);

        assertThat(request.getDescription()).isEqualTo("Vazamento no banheiro social");
        assertThat(request.getStatus()).isEqualTo(ServiceRequestStatus.PENDING);
        assertThat(request.getClient().getId()).isEqualTo(client.getId());
        assertThat(request.getProvider().getId()).isEqualTo(provider.getId());
        assertThat(request.getMediaUrl()).isNotNull();
        assertThat(request.getMediaUrl()).contains("marketplace-files");
        assertThat(request.getMediaUrl()).contains("problema_cano.jpg");

        // 4. Validar se o arquivo realmente existe no MinIO/S3
        // Extrair a key do final da URL: http://localhost:9000/marketplace-files/<key>
        String[] parts = request.getMediaUrl().split("/");
        String fileKey = parts[parts.length - 1];

        assertThat(doesObjectExist("marketplace-files", fileKey)).isTrue();
    }

    @Test
    @DisplayName("Deve criar um chamado apenas com texto (sem arquivo)")
    void shouldCreateServiceRequestWithoutFile() throws Exception {
        mockMvc.perform(multipart("/api/v1/services/requests")
                        .param("description", "Apenas descrição em texto")
                        .param("providerId", provider.getId().toString())
                        .header("Authorization", clientToken))
                .andExpect(status().isCreated());

        var requests = serviceRequestRepository.findAll();
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getMediaUrl()).isNull();
    }

    @Test
    @DisplayName("Deve bloquear prestador de abrir chamados (apenas ROLE_CLIENT)")
    void shouldBlockProviderFromCreatingRequest() throws Exception {
        String providerToken = "Bearer " + jwtTokenService.generateToken(provider);

        mockMvc.perform(multipart("/api/v1/services/requests")
                        .param("description", "Tentativa proibida")
                        .param("providerId", provider.getId().toString())
                        .header("Authorization", providerToken))
                .andExpect(status().isForbidden());
    }

    private boolean doesObjectExist(String bucket, String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
