package com.marketplace.ceara.model.enums;

/**
 * Estados possíveis de um chamado de serviço (US04).
 */
public enum ServiceRequestStatus {
    PENDING,    // Aguardando avaliação do prestador
    ACCEPTED,   // Aceito pelo prestador
    REJECTED,   // Recusado pelo prestador
    CANCELED,   // Cancelado pelo cliente
    COMPLETED,  // Finalizado
    PAYMENT_CONFIRMED // Pagamento garantido na plataforma (Escrow)
}
