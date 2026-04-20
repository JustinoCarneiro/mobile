# 1. Requisitos do Sistema - Marketplace Ceará

## Épicos e Histórias de Usuário
- [cite_start]**Identidade (US01/US02):** Cadastro e login via e-mail/senha com validação de CPF para prestadores[cite: 2, 7].
- [cite_start]**Descoberta (US03):** Visualização de profissionais por categoria e proximidade[cite: 1, 7].
- [cite_start]**Solicitação (US04):** Abertura de chamados multimídia com texto, áudio ou foto[cite: 5, 7].
- [cite_start]**Pagamento (US05/06/07):** Checkout via Pix/Cartão com retenção financeira (Escrow)[cite: 3, 7].
- [cite_start]**Reputação (US08):** Sistema de avaliação de 1 a 5 estrelas e comentários[cite: 4, 7].

## Requisitos Não Funcionais (NFs)
- [cite_start]**Performance:** Resposta de telas em menos de 300ms[cite: 8].
- [cite_start]**Escalabilidade:** Uso de Virtual Threads no Java 21 para suportar picos de concorrência[cite: 7, 8].
- [cite_start]**Atomicidade:** Garantia de rollback em falhas financeiras usando @Transactional[cite: 7, 8].
- [cite_start]**Infraestrutura:** Setup 100% Linux-native via Docker[cite: 6, 8].