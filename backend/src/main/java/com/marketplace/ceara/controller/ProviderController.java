package com.marketplace.ceara.controller;

import com.marketplace.ceara.dto.provider.NearbyProviderDTO;
import com.marketplace.ceara.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para operações relacionadas aos prestadores.
 */
@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    /**
     * US03 — Busca geolocalizada de prestadores.
     * Requer JWT válido (definido no SecurityConfig).
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyProviderDTO>> getNearbyProviders(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam String category,
            @RequestParam(defaultValue = "10.0") Double radius
    ) {
        List<NearbyProviderDTO> providers = providerService.findNearby(lat, lng, category, radius);
        return ResponseEntity.ok(providers);
    }
}
