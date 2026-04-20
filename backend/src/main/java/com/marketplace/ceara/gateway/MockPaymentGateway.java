package com.marketplace.ceara.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock para simular integração com gateway de pagamento (US05).
 */
@Component
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public PaymentIntent createCheckout(UUID internalTransactionId, BigDecimal amount) {
        log.info("[PaymentGateway] Gerando checkout fictício: txId={}, amount={}", internalTransactionId, amount);
        
        // Simula gateway_id único e uma URL de pagamento/QR Code Pix
        String gatewayId = "GTW_" + UUID.randomUUID().toString().substring(0, 8);
        String paymentUrl = "https://mockgateway.com/pay/" + gatewayId;
        
        return new PaymentIntent(gatewayId, paymentUrl);
    }
}
