package com.marketplace.ceara.service;

import com.marketplace.ceara.gateway.PaymentGateway;
import com.marketplace.ceara.model.ServiceRequest;
import com.marketplace.ceara.model.Transaction;
import com.marketplace.ceara.model.enums.ServiceRequestStatus;
import com.marketplace.ceara.model.enums.TransactionStatus;
import com.marketplace.ceara.repository.ServiceRequestRepository;
import com.marketplace.ceara.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Serviço responsável por pagamentos e gestão de Escrow (US05, US06).
 */
@Service
@SuppressWarnings("null")
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final TransactionRepository transactionRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final PaymentGateway paymentGateway;

    public PaymentService(TransactionRepository transactionRepository,
                          ServiceRequestRepository serviceRequestRepository,
                          PaymentGateway paymentGateway) {
        this.transactionRepository = transactionRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.paymentGateway = paymentGateway;
    }

    /**
     * Inicia o processo de checkout para um chamado.
     */
    @Transactional
    public Transaction initiateCheckout(UUID serviceRequestId, BigDecimal amount) {
        log.info("Iniciando checkout: serviceRequestId={}, amount={}", serviceRequestId, amount);

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação de serviço não encontrada"));

        if (serviceRequest.getStatus() != ServiceRequestStatus.ACCEPTED) {
            throw new IllegalStateException("O serviço deve estar ACEITO para processar o pagamento");
        }

        // Criar transação PENDING
        Transaction transaction = new Transaction(serviceRequest, amount, null);
        transaction = transactionRepository.save(transaction);

        // Integrar com o Gateway
        var intent = paymentGateway.createCheckout(transaction.getId(), amount);
        transaction.setGatewayId(intent.gatewayId());
        
        log.info("Checkout criado via Gateway: gatewayId={}", transaction.getGatewayId());
        return transactionRepository.save(transaction);
    }

    /**
     * Processa a confirmação de pagamento vinda do Webhook (US06).
     * Operação ATÔMICA: Atualiza Transação e Chamado.
     */
    @Transactional
    public void processPaymentConfirmation(String gatewayId) {
        log.info("[Webhook] Confirmando pagamento para gatewayId={}", gatewayId);

        Transaction transaction = transactionRepository.findByGatewayId(gatewayId)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada para o gatewayId: " + gatewayId));

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.warn("Transação {} já processada: status={}", transaction.getId(), transaction.getStatus());
            return;
        }

        // 1. Atualizar status da transação para PAID_ESCROW
        transaction.setStatus(TransactionStatus.PAID_ESCROW);
        transactionRepository.save(transaction);

        // 2. Atualizar status do serviço para PAYMENT_CONFIRMED
        ServiceRequest serviceRequest = transaction.getServiceRequest();
        serviceRequest.setStatus(ServiceRequestStatus.PAYMENT_CONFIRMED);
        serviceRequestRepository.save(serviceRequest);

        log.info("PAGAMENTO GARANTIDO (Escrow): txId={}, serviceId={}", transaction.getId(), serviceRequest.getId());
    }
    /**
     * Libera o pagamento retido em Escrow para o prestador (US07).
     * Exige que o serviço esteja COMPLETED.
     */
    @Transactional
    public void releaseFunds(UUID requestId) {
        log.info("Iniciando liberação de fundos para solicitação: {}", requestId);

        ServiceRequest serviceRequest = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada"));

        if (serviceRequest.getStatus() != ServiceRequestStatus.COMPLETED) {
            throw new IllegalStateException("O serviço deve estar marcado como CONCLUÍDO para liberar o pagamento");
        }

        Transaction transaction = transactionRepository.findByServiceRequestId(requestId)
                .orElseThrow(() -> new IllegalStateException("Transação não encontrada para este pedido"));

        if (transaction.getStatus() != TransactionStatus.PAID_ESCROW) {
            throw new IllegalStateException("O pagamento não está retido em Escrow. Status atual: " + transaction.getStatus());
        }

        // 1. Atualizar transação para RELEASED
        transaction.setStatus(TransactionStatus.RELEASED);
        transactionRepository.save(transaction);

        // O status do ServiceRequest permanece COMPLETED, mas o ciclo financeiro fecha.
        log.info("PAGAMENTO LIBERADO para o prestador. txId={}", transaction.getId());
    }
}
