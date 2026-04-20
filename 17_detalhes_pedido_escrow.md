# 17. Detalhes do Pedido e Liberação de Escrow (US06 e US07)

## 1. Objetivo
Permitir que o cliente visualize os detalhes completos de um serviço e libere o pagamento após a conclusão.

## 2. Requisitos de Tela
- **Informações do Serviço:** Descrição completa e miniatura da mídia enviada (se houver).
- **Status do Escrow:** Exibir claramente se o dinheiro está "Garantido pela Plataforma".
- **Ações Dinâmicas:**
    - Se Status = `PAGAMENTO_CONFIRMADO`: Exibir mensagem de "Aguardando conclusão do serviço".
    - Se Status = `CONCLUIDO`: Exibir o botão **"Liberar Pagamento"**.
    - Após liberação: Navegar automaticamente para a tela de **Avaliação (US08)**.

## 3. Lógica Financeira
- **Botão Liberar:** Aciona o endpoint `POST /api/v1/payments/release/{id}`.
- **Segurança:** O botão só deve ser clicável se o prestador já tiver marcado o serviço como concluído no lado dele.

## 4. UI/UX
- Utilizar os componentes base `<Button />` e `<Input />`.
- Exibir um alerta de confirmação antes de liberar o dinheiro.