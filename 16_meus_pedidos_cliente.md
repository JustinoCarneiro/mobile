# 16. Especificação: Dashboard "Meus Pedidos" (Cliente)

## 1. Objetivo
Visualizar e gerenciar todas as solicitações de serviço enviadas pelo cliente logado.

## 2. Requisitos de Tela
- **Listagem:** Utilizar `FlatList` para exibir os pedidos ordenados pelo mais recente.
- **Card de Pedido:** Exibir:
    - Nome do Prestador.
    - Data da solicitação.
    - Status atual (com cores distintas por status).
    - Descrição curta.
- **Filtros (Opcional):** Abas para filtrar por 'Em Aberto' e 'Finalizados'.

## 3. Lógica de Status (Cores)
- `PENDENTE`: Amarelo (Aguardando prestador).
- `ACEITO` / `EM_ANDAMENTO`: Azul (Serviço em curso).
- `CONCLUIDO`: Verde (Aguardando avaliação/pagamento).
- `CANCELADO`: Vermelho.

## 4. Integração
- **Endpoint:** `GET /api/v1/services/my-requests` (necessário garantir que o Back-end filtre pelo `clientId` do token).
- **Refresh:** Implementar `onRefresh` para atualizar os status manualmente.