# 14. Especificação: Home e Busca Geolocalizada (US03)

## 1. História de Usuário
- [cite_start]**US03:** Como Cliente, eu quero visualizar uma lista de profissionais por categoria e proximidade, para encontrar ajuda rápida perto da minha casa. 

## 2. Requisitos de Front-end
- [cite_start]**Captura de Localização:** Utilizar `expo-location` para obter as coordenadas (lat/lng) do dispositivo de forma nativa. [cite: 15, 205]
- [cite_start]**Listagem de Categorias:** Exibir um carrossel ou grade de categorias (ex: Encanador, Eletricista, Diarista). 
- [cite_start]**Resultados de Busca:** Exibir cards dos prestadores contendo: Nome, Categoria, Nota Média e a Distância em KM calculada pelo Back-end. 

## 3. Lógica de Integração
- [cite_start]**Chamada de API:** `GET /api/v1/providers/nearby?lat={lat}&lng={lng}&category={id}`. 
- [cite_start]**Estado Global:** Armazenar as coordenadas atuais do usuário no `authStore` ou em um hook especializado para evitar pedidos de permissão repetitivos. [cite: 228]

## 4. UI/UX (NativeWind)
- **Loading State:** Exibir um esqueleto (Skeleton) ou Spinner enquanto a localização está sendo obtida ou os dados estão sendo baixados.
- **Empty State:** Exibir mensagem amigável caso nenhum prestador seja encontrado no raio de busca.