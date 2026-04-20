package com.marketplace.ceara.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidade que registra a avaliação de um serviço por um cliente (US08).
 * Cada serviço (ServiceRequest) só pode ser avaliado uma única vez.
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "service_request_id", nullable = false, unique = true)
    private ServiceRequest serviceRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    protected Review() {}

    public Review(ServiceRequest serviceRequest, User client, User provider, Integer rating, String comment) {
        this.serviceRequest = serviceRequest;
        this.client = client;
        this.provider = provider;
        this.rating = rating;
        this.comment = comment;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public ServiceRequest getServiceRequest() { return serviceRequest; }
    public User getClient() { return client; }
    public User getProvider() { return provider; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
}
