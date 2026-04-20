package com.marketplace.ceara.model;

import com.marketplace.ceara.model.enums.VerificationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Perfil profissional do prestador de serviços.
 * Criado atomicamente junto ao User na transação de registro.
 */
@Entity
@Table(name = "providers_profile")
public class ProviderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column
    private String category;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "rating_average", precision = 3, scale = 2)
    private BigDecimal ratingAverage = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    protected ProviderProfile() {}

    public ProviderProfile(User user, String category, String bio, Double latitude, Double longitude) {
        this.user = user;
        this.category = category;
        this.bio = bio;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // -------------------------------------------------------------------------
    // Getters / setters mínimos
    // -------------------------------------------------------------------------

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public String getCategory() { return category; }
    public String getBio() { return bio; }
    public BigDecimal getRatingAverage() { return ratingAverage; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setVerificationStatus(VerificationStatus status) {
        this.verificationStatus = status;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }
}
