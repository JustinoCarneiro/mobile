package com.marketplace.ceara;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test: valida que o contexto Spring sobe corretamente.
 */
@SpringBootTest
@ActiveProfiles("test")
class MarketplaceCearaApplicationTests {

    @Test
    void contextLoads() {
        // Valida que o contexto Spring inicializa sem erros
    }
}
