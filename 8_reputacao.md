# 8. Especificação: Sistema de Avaliação (US08)

## 1. História de Usuário
- **US08:** Como Cliente, eu quero dar de 1 a 5 estrelas e deixar um comentário na página do prestador, para ajudar outros usuários a fazerem boas escolhas.

## 2. Regras de Negócio
- **Elegibilidade:** Apenas clientes que tiveram um serviço marcado como `CONCLUIDO` podem avaliar aquele serviço específico.
- **Unicidade:** Cada serviço só pode ser avaliado uma única vez.
- [cite_start]**Cálculo de Média:** Ao salvar uma nova avaliação, a nota média (`rating_average`) no `ProviderProfile` deve ser recalculada de forma assíncrona. 
- [cite_start]**Escopo da Nota:** A nota deve ser um número inteiro de 1 a 5. [cite: 101]

## 3. Especificação Técnica
- [cite_start]**Entidade `Review`:** `id` (UUID), `service_request_id` (FK), `client_id` (FK), `provider_id` (FK), `rating` (Integer), `comment` (String). 
- [cite_start]**Endpoint:** `POST /api/v1/services/{id}/reviews` [cite: 107]
- **Segurança:** Acesso restrito a usuários com `ROLE_CLIENT`.