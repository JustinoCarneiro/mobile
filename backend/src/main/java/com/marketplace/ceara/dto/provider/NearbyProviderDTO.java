package com.marketplace.ceara.dto.provider;

import java.math.BigDecimal;

/**
 * DTO para retorno de prestadores próximos (US03).
 * Inclui o cálculo da distância em quilômetros.
 */
public record NearbyProviderDTO(
    String fullName,
    String category,
    BigDecimal ratingAverage,
    BigDecimal distance
) {}
