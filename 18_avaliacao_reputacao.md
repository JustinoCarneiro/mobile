# 18. Especificação: Tela de Avaliação (US08)

## 1. Objetivo
Permitir que o cliente avalie a experiência do serviço com uma nota (1 a 5 estrelas) e um comentário textual.

## 2. Requisitos de UI
- **Sistema de Estrelas:** Componente interativo para seleção de nota (Integer).
- **Campo de Texto:** Área para o comentário (opcional).
- **Botão de Envio:** Aciona o endpoint de reviews.
- **Feedback:** Após o envio, mostrar uma mensagem de "Obrigado!" e retornar para a Home.

## 3. Integração Técnica
- **Endpoint:** `POST /api/v1/services/{id}/reviews`.
- **Payload:** `{ "rating": number, "comment": string }`.
- **Efeito Colateral:** O Back-end já está configurado para recalcular a nota média do prestador de forma assíncrona após este post.

## 4. UX
- Impedir o envio se nenhuma estrela for selecionada.
- Usar o componente `<Button />` com `isLoading` durante a postagem.