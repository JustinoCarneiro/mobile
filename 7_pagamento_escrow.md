# 7. Especificação: Checkout e Escrow (US05, US06 e US07)

## 1. Histórias de Usuário
- [cite_start]**US05:** Pagamento via Pix/Cartão de Crédito[cite: 70, 207].
- [cite_start]**US06:** Retenção do valor na plataforma (Escrow) até a conclusão do serviço[cite: 70, 208].
- [cite_start]**US07:** Visualização do status "Pagamento Garantido" para o prestador[cite: 71, 209].

## 2. Modelagem de Dados (Entidade Transaction)
- [cite_start]**Transaction:** `id` (UUID), `service_request_id` (FK), `amount`, `status` (ENUM: `PENDING`, `PAID_ESCROW`, `RELEASED`, `REFUNDED`), `gateway_id`.

## 3. Regras de Negócio e Blindagem Financeira
- [cite_start]**Atomicidade:** O status da transação e do serviço deve ser alterado sob a anotação `@Transactional`.
- **Segurança:** O valor pago pelo cliente **não** é enviado ao prestador imediatamente. [cite_start]Ele permanece no status `PAID_ESCROW`[cite: 71, 209].
- [cite_start]**Webhook:** O sistema deve estar preparado para receber notificações do Gateway de Pagamento para confirmar a transação[cite: 81, 252].
- [cite_start]**Mock de Integração:** Nesta fase, simularemos o Gateway de Pagamento para evitar custos e dependências externas[cite: 252].