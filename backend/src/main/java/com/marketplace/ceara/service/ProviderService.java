package com.marketplace.ceara.service;

import com.marketplace.ceara.dto.provider.NearbyProviderDTO;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de gestão de prestadores.
 * Implementa a lógica de busca geolocalizada (US03).
 */
@Service
public class ProviderService {

    private final ProviderProfileRepository providerProfileRepository;

    public ProviderService(ProviderProfileRepository providerProfileRepository) {
        this.providerProfileRepository = providerProfileRepository;
    }

    /**
     * Busca prestadores próximos filtrados por categoria e raio de distância.
     */
    @Transactional(readOnly = true)
    public List<NearbyProviderDTO> findNearby(Double lat, Double lng, String category, Double radius) {
        return providerProfileRepository.findNearby(lat, lng, category, radius)
                .stream()
                .map(p -> new NearbyProviderDTO(
                        p.getFullName(),
                        p.getCategory(),
                        p.getRatingAverage(),
                        p.getDistance()
                ))
                .collect(Collectors.toList());
    }
}
