# 20. Protocolo de Teste: Ciclo de Vida Completo (E2E)

## 1. Cenário de Teste
Validar a jornada do usuário desde a abertura do chamado até o recalculo da reputação do prestador.

## 2. Roteiro de Execução (Passo a Passo)

### Fase 1: O Cliente Solicita
1. **Login:** Usuário A loga como `ROLE_CLIENT`.
2. **Busca:** Localiza o Prestador B via `HomeScreen` (Geolocalização).
3. **Chamado:** Abre uma solicitação com descrição e uma foto de teste.

### Fase 2: O Prestador Gerencia
4. **Login:** Usuário B loga como `ROLE_PROVIDER` (Redirecionamento automático).
5. **Ações:** O prestador visualiza o novo pedido na aba "Novos", clica em **Aceitar** e depois em **Concluir**.

### Fase 3: Liquidação e Feedback
6. **Checkout:** Usuário A volta ao app, vê o status `CONCLUIDO` e clica em **Liberar Pagamento**.
7. **Reputação:** Usuário A avalia o prestador com 5 estrelas e um comentário.
8. **Validação Final:** Verificar se a nota média do Prestador B foi atualizada no banco/front.