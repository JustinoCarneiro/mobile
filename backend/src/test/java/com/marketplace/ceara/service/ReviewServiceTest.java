package com.marketplace.ceara.service;

import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.ServiceRequestStatus;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import com.marketplace.ceara.repository.ReviewRepository;
import com.marketplace.ceara.repository.ServiceRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService — Testes de Unidade (US08)")
@SuppressWarnings("null")
class ReviewServiceTest {

    @Mock ReviewRepository reviewRepository;
    @Mock ServiceRequestRepository serviceRequestRepository;
    @Mock ProviderProfileRepository providerProfileRepository;

    @InjectMocks ReviewService reviewService;

    @Test
    @DisplayName("Deve impedir avaliação se o status não for COMPLETED")
    void shouldFailIfStatusNotCompleted() {
        UUID serviceId = UUID.randomUUID();
        ServiceRequest sr = mock(ServiceRequest.class);
        when(sr.getStatus()).thenReturn(ServiceRequestStatus.ACCEPTED);
        when(serviceRequestRepository.findById(serviceId)).thenReturn(Optional.of(sr));

        Exception exception = assertThrows(IllegalStateException.class, () -> 
                reviewService.createReview(serviceId, UUID.randomUUID(), 5, "Bom")
        );

        assertEquals("Apenas serviços CONCLUIDOS podem ser avaliados", exception.getMessage());
    }

    @Test
    @DisplayName("Deve impedir avaliação se o cliente não for o autor do pedido")
    void shouldFailIfClientIsNotAuthor() {
        UUID serviceId = UUID.randomUUID();
        UUID authClientId = UUID.randomUUID();
        UUID ownerClientId = UUID.randomUUID();

        ServiceRequest sr = mock(ServiceRequest.class);
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(ownerClientId);
        
        when(sr.getStatus()).thenReturn(ServiceRequestStatus.COMPLETED);
        when(sr.getClient()).thenReturn(owner);
        when(serviceRequestRepository.findById(serviceId)).thenReturn(Optional.of(sr));

        assertThrows(SecurityException.class, () -> 
                reviewService.createReview(serviceId, authClientId, 5, "Bom")
        );
    }
}
