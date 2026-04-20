package com.marketplace.ceara.controller;

import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.service.ServiceRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Controller para gestão de solicitações de serviço.
 */
@RestController
@RequestMapping("/api/v1/services/requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    /**
     * US04 — Abertura de chamado multimídia.
     * Aceita multipart/form-data.
     */
    @PostMapping
    public ResponseEntity<ServiceRequest> createRequest(
            @RequestParam("description") String description,
            @RequestParam("providerId") UUID providerId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal User client
    ) {
        ServiceRequest request = serviceRequestService.createRequest(
                client.getId(),
                providerId,
                description,
                file
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }
    /**
     * US16 — Listagem de pedidos do cliente logado.
     */
    @GetMapping("/my-requests")
    public ResponseEntity<java.util.List<ServiceRequest>> getMyRequests(
            @AuthenticationPrincipal User client
    ) {
        return ResponseEntity.ok(serviceRequestService.getClientRequests(client.getId()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequest> getRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(serviceRequestService.getRequestById(id));
    }

    @GetMapping("/provider/requests")
    public ResponseEntity<java.util.List<ServiceRequest>> getProviderRequests(
            @AuthenticationPrincipal User provider
    ) {
        return ResponseEntity.ok(serviceRequestService.getProviderRequests(provider.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ServiceRequest> updateStatus(
            @PathVariable UUID id,
            @RequestBody StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(serviceRequestService.updateStatus(id, request.status()));
    }

    public record StatusUpdateRequest(com.marketplace.ceara.model.enums.ServiceRequestStatus status) {}
}
