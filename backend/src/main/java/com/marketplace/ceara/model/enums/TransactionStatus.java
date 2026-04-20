package com.marketplace.ceara.model.enums;

/**
 * Estados do ciclo de vida de uma transação financeira (US06).
 */
public enum TransactionStatus {
    PENDING,        // Checkout iniciado, aguardando pagamento
    PAID_ESCROW,    // Pago e retido pela plataforma
    RELEASED,       // Valor liberado para o prestador
    REFUNDED        // Valor estornado ao cliente
}
