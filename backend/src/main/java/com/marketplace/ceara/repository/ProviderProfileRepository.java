package com.marketplace.ceara.repository;

import com.marketplace.ceara.model.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UUID> {

    Optional<ProviderProfile> findByUserId(UUID userId);

    @Query(value = """
        SELECT u.full_name AS fullName, 
               p.category AS category, 
               p.rating_average AS ratingAverage,
               (6371 * acos(
                   cos(radians(:lat)) * cos(radians(p.latitude)) * 
                   cos(radians(p.longitude) - radians(:lng)) + 
                   sin(radians(:lat)) * sin(radians(p.latitude))
               ))::numeric AS distance
        FROM providers_profile p
        JOIN users u ON p.user_id = u.id
        WHERE p.verification_status = 'APPROVED' 
          AND p.category = :category
          AND (6371 * acos(
                   cos(radians(:lat)) * cos(radians(p.latitude)) * 
                   cos(radians(p.longitude) - radians(:lng)) + 
                   sin(radians(:lat)) * sin(radians(p.latitude))
               )) <= :radius
        ORDER BY distance ASC
        """, nativeQuery = true)
    List<NearbyProviderProjection> findNearby(Double lat, Double lng, String category, Double radius);

    /**
     * Projeção para capturar resultados da busca geolocalizada.
     */
    interface NearbyProviderProjection {
        String getFullName();
        String getCategory();
        java.math.BigDecimal getRatingAverage();
        java.math.BigDecimal getDistance();
    }
}
