package com.marketplace.ceara.model;

import com.marketplace.ceara.model.enums.ServiceRequestStatus;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Representa uma solicitação de serviço feita por um cliente a um prestador (US04).
 * Suporta descrição em texto e anexo multimídia (URL).
 */
@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "provider_id")
    private User provider;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "media_url")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceRequestStatus status = ServiceRequestStatus.PENDING;

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    protected ServiceRequest() {}

    public ServiceRequest(User client, User provider, String description, String mediaUrl) {
        this.client = client;
        this.provider = provider;
        this.description = description;
        this.mediaUrl = mediaUrl;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public User getClient() { return client; }
    public User getProvider() { return provider; }
    public String getDescription() { return description; }
    public String getMediaUrl() { return mediaUrl; }
    public ServiceRequestStatus getStatus() { return status; }

    public void setStatus(ServiceRequestStatus status) {
        this.status = status;
    }
}
