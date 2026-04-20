# 5. Especificação: Motor de Busca Geolocalizado (US03)

## 1. Objetivo
Implementar a busca de prestadores de serviço baseada na localização geográfica do cliente (Latitude/Longitude) e categoria do serviço.

## 2. Requisitos de Negócio
- **Filtros Obrigatórios:** `latitude`, `longitude` e `categoria`.
- **Raio de Busca:** O sistema deve filtrar prestadores dentro de um raio (default 10km).
- **Ordenação:** Os resultados devem ser entregues do mais próximo para o mais distante.

## 3. Especificação Técnica
- **Cálculo Espacial:** A fórmula de Haversine deve ser executada via SQL Nativo no PostgreSQL para evitar o carregamento de dados desnecessários na memória da JVM.
- **Performance:** A query deve ser otimizada para responder em menos de 300ms.
- **DTO de Saída:** `NearbyProviderDTO` contendo nome, categoria, nota média e a distância calculada em quilômetros.