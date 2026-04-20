package com.marketplace.ceara.service;

import com.marketplace.ceara.model.ProviderProfile;
import com.marketplace.ceara.model.Review;
import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.enums.ServiceRequestStatus;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import com.marketplace.ceara.repository.ReviewRepository;
import com.marketplace.ceara.repository.ServiceRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Serviço de avaliações e reputação (US08).
 * Gerencia a criação de reviews e o recálculo da média do prestador.
 */
@Service
@SuppressWarnings("null")
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public ReviewService(ReviewRepository reviewRepository,
            ServiceRequestRepository serviceRequestRepository,
            ProviderProfileRepository providerProfileRepository) {
        this.reviewRepository = reviewRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    /**
     * Cria uma nova avaliação para um serviço concluído.
     */
    @Transactional
    public Review createReview(UUID serviceRequestId, UUID clientId, Integer rating, String comment) {
        log.info("Iniciando criação de review: serviceRequestId={}, clientId={}, rating={}", serviceRequestId, clientId,
                rating);

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação de serviço não encontrada"));

        // 1. Validação de Elegibilidade
        if (serviceRequest.getStatus() != ServiceRequestStatus.COMPLETED) {
            throw new IllegalStateException("Apenas serviços CONCLUIDOS podem ser avaliados");
        }

        // 2. Validação de Autoria
        if (!serviceRequest.getClient().getId().equals(clientId)) {
            throw new SecurityException("Você só pode avaliar serviços que você mesmo solicitou");
        }

        // 3. Validação de Unicidade
        if (reviewRepository.existsByServiceRequestId(serviceRequestId)) {
            throw new IllegalStateException("Este serviço já foi avaliado");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("A nota deve ser entre 1 e 5");
        }

        Review review = new Review(
                serviceRequest,
                serviceRequest.getClient(),
                serviceRequest.getProvider(),
                rating,
                comment);

        Review savedReview = reviewRepository.save(review);
        log.info("Review salva com sucesso: id={}", savedReview.getId());

        // 4. Recálculo assíncrono da média
        updateProviderRatingAverage(serviceRequest.getProvider().getId());

        return savedReview;
    }

    /**
     * Atualiza a nota média do prestador de forma assíncrona usando Virtual
     * Threads.
     */
    @Async("virtualThreadExecutor")
    @Transactional
    public void updateProviderRatingAverage(UUID providerId) {
        log.info("[Async] Recalculando média para prestadorId={}", providerId);

        List<Integer> ratings = reviewRepository.findAllRatingsByProviderId(providerId);

        if (ratings.isEmpty())
            return;

        double sum = ratings.stream().mapToInt(Integer::intValue).sum();
        BigDecimal average = BigDecimal.valueOf(sum / ratings.size())
                .setScale(2, RoundingMode.HALF_UP);

        ProviderProfile profile = providerProfileRepository.findByUserId(providerId)
                .orElseThrow(
                        () -> new RuntimeException("Perfil do prestador não encontrado para o usuário: " + providerId));

        log.info("[Async] Nova média calculada: {} (total de {} avaliações)", average, ratings.size());

        profile.setRatingAverage(average);
        providerProfileRepository.save(profile);
    }
}
