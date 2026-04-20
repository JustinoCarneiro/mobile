# 15. Especificação: Solicitação de Serviço (US04)

## 1. História de Usuário
- **US04:** Como Cliente, eu quero solicitar um serviço enviando um texto e uma foto do meu problema, para que o prestador avalie a gravidade.

## 2. Fluxo de Navegação
- **Origem:** Ao clicar em um `ProviderCard` na Home.
- **Tela:** `ServiceRequestScreen` (Nova).
- **Destino:** Após envio, retornar para a Home ou aba de 'Meus Pedidos'.

## 3. Componentes da Tela
- **Header:** Nome e Categoria do Prestador selecionado.
- **Input de Texto:** Área para descrição detalhada do problema.
- **Upload de Mídia:** Botão para selecionar foto da galeria (usando `expo-image-picker`).
- **Botão de Envio:** Aciona o endpoint `POST /api/v1/services/requests`.

## 4. Detalhes Técnicos
- **Multipart/Form-Data:** O envio deve conter o texto e o arquivo binário.
- **Feedback Visual:** Barra de progresso ou Spinner durante o upload para o S3/MinIO via Back-end.