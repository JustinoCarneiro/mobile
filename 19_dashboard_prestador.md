# 19. Especificação: Dashboard do Prestador (US09, US10 e US11)

## 1. Objetivo
Permitir que o profissional gerencie suas solicitações recebidas, aceite novos trabalhos e atualize o progresso do serviço.

## 2. Requisitos de Tela
- **Listagem de Pedidos Recebidos:** Exibir solicitações com status `PENDENTE`.
- **Card de Solicitação:** Mostrar descrição, foto (se houver) e localização aproximada do cliente.
- **Ações de Fluxo (Botões):**
    - `ACEITAR`: Muda status para `ACEITO`.
    - `INICIAR`: Muda status para `EM_ANDAMENTO`.
    - `CONCLUIR`: Muda status para `CONCLUIDO` (liberando a visão de pagamento para o cliente).

## 3. Lógica de Navegação
- O App deve identificar a `Role` do usuário no login.
- Se `ROLE_PROVIDER`, a tela inicial (Home) deve ser este Dashboard em vez da busca de profissionais.

## 4. UI/UX
- Utilizar os componentes `<Button />` e `<Input />` já padronizados.
- Exibir abas de "Novos" e "Em Andamento" para organizar o trabalho do profissional.