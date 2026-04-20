package com.marketplace.ceara.service;

import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.Role;
import com.marketplace.ceara.repository.ServiceRequestRepository;
import com.marketplace.ceara.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Serviço de gestão de solicitações de serviço (US04).
 */
@Service
@SuppressWarnings("null")
public class ServiceRequestService {

    private static final Logger log = LoggerFactory.getLogger(ServiceRequestService.class);

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Cria um novo chamado de serviço.
     * Realiza o upload da mídia antes de persistir no banco.
     */
    @Transactional
    public ServiceRequest createRequest(UUID clientId, UUID providerId, String description, MultipartFile file) {
        log.info("Iniciando criação de chamado: clienteId={}, prestadorId={}", clientId, providerId);

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Prestador não encontrado"));

        if (client.getRole() != Role.CLIENT) {
            throw new IllegalArgumentException("Apenas clientes podem abrir chamados");
        }

        if (provider.getRole() != Role.PROVIDER) {
            throw new IllegalArgumentException("O destino da solicitação deve ser um prestador");
        }

        // 1. Upload do arquivo para o Storage (se houver)
        String mediaUrl = null;
        if (file != null && !file.isEmpty()) {
            mediaUrl = fileStorageService.upload(file);
        }

        // 2. Persistência no banco de dados
        ServiceRequest request = new ServiceRequest(client, provider, description, mediaUrl);
        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        log.info("Chamado criado com sucesso: id={}", savedRequest.getId());
        return savedRequest;
    }

    @Transactional(readOnly = true)
    public java.util.List<ServiceRequest> getClientRequests(UUID clientId) {
        log.info("Buscando solicitações do cliente: id={}", clientId);
        return serviceRequestRepository.findByClientIdOrderByStatusAsc(clientId);
    }

    @Transactional(readOnly = true)
    public ServiceRequest getRequestById(UUID id) {
        log.info("Buscando solicitação por ID: {}", id);
        return serviceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public java.util.List<ServiceRequest> getProviderRequests(UUID providerId) {
        log.info("Buscando solicitações recebidas pelo prestador: id={}", providerId);
        return serviceRequestRepository.findByProviderIdOrderByStatusAsc(providerId);
    }

    @Transactional
    public ServiceRequest updateStatus(UUID requestId, com.marketplace.ceara.model.enums.ServiceRequestStatus status) {
        log.info("Atualizando status da solicitação: id={}, novoStatus={}", requestId, status);
        ServiceRequest request = getRequestById(requestId);
        request.setStatus(status);
        return serviceRequestRepository.save(request);
    }
}
