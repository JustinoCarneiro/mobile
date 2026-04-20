package com.marketplace.ceara.repository;

import com.marketplace.ceara.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {
    List<ServiceRequest> findByClientIdOrderByStatusAsc(UUID clientId);
    List<ServiceRequest> findByProviderIdOrderByStatusAsc(UUID providerId);
}
