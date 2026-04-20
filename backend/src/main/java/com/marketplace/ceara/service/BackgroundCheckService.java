package com.marketplace.ceara.service;

import com.marketplace.ceara.model.enums.VerificationStatus;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Serviço de Background Check simulado.
 *
 * O método runCheck é executado em uma Virtual Thread (configurada em
 * AsyncConfig),
 * simulando uma chamada I/O bloqueante (ex: API externa de validação de CPF).
 *
 * Após os 5 segundos, o status do prestador é atualizado para APPROVED.
 * O @Transactional garante que a atualização seja commitada de forma
 * independente
 * da transação original de registro.
 */
@Service
public class BackgroundCheckService {

    private static final Logger log = LoggerFactory.getLogger(BackgroundCheckService.class);

    private final ProviderProfileRepository providerProfileRepository;

    public BackgroundCheckService(ProviderProfileRepository providerProfileRepository) {
        this.providerProfileRepository = providerProfileRepository;
    }

    @Async("virtualThreadExecutor")
    @Transactional
    public void runCheck(UUID userId) {
        log.info("[BackgroundCheck] Iniciando verificação para userId={} em thread={}",
                userId, Thread.currentThread());

        try {
            // Simula latência de I/O bloqueante (ex: chamada a API externa de CPF)
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[BackgroundCheck] Interrompido para userId={}", userId);
            return;
        }

        providerProfileRepository.findByUserId(userId).ifPresentOrElse(
                profile -> {
                    profile.setVerificationStatus(VerificationStatus.APPROVED);
                    providerProfileRepository.save(profile);
                    log.info("[BackgroundCheck] APPROVED para userId={}", userId);
                },
                () -> log.warn("[BackgroundCheck] Perfil não encontrado para userId={}", userId));
    }
}
