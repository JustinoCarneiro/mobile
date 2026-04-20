package com.marketplace.ceara.controller;

import com.marketplace.ceara.model.Transaction;
import com.marketplace.ceara.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Controller para operações financeiras e checkout (US05, US06).
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Inicia o checkout de um serviço.
     */
    @PostMapping("/checkout")
    public ResponseEntity<Transaction> checkout(@RequestBody CheckoutRequest request) {
        Transaction transaction = paymentService.initiateCheckout(request.serviceRequestId(), request.amount());
        return ResponseEntity.ok(transaction);
    }

    /**
     * Webhook simulado para recebimento de confirmação do gateway.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody WebhookPayload payload) {
        paymentService.processPaymentConfirmation(payload.gateway_id());
        return ResponseEntity.ok().build();
    }

    /**
     * Libera o pagamento retido em Escrow (US07).
     */
    @PostMapping("/release/{id}")
    public ResponseEntity<Void> release(@PathVariable UUID id) {
        paymentService.releaseFunds(id);
        return ResponseEntity.ok().build();
    }

    // DTOs auxiliares
    public record CheckoutRequest(UUID serviceRequestId, BigDecimal amount) {}
    public record WebhookPayload(String gateway_id, String status) {}
}
