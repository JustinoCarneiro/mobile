package com.marketplace.ceara.gateway;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Interface para integração com gateways de pagamento (Pix/Cartão).
 */
public interface PaymentGateway {
    
    /**
     * Gera uma intenção de pagamento.
     * @return ID único do gateway e dados para pagamento (ex: QR Code Pix).
     */
    PaymentIntent createCheckout(UUID internalTransactionId, BigDecimal amount);

    record PaymentIntent(String gatewayId, String paymentUrl) {}
}
