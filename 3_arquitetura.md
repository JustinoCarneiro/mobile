# 3. Arquitetura Técnica e Modelagem

## Stack Tecnológica
- [cite_start]**Back-end:** Spring Boot 3.x e Java 21[cite: 6, 7].
- [cite_start]**Front-end:** React Native com Expo[cite: 6, 7].
- [cite_start]**Persistência:** PostgreSQL[cite: 6, 7].
- [cite_start]**Storage Local:** MinIO (Substituindo temporariamente o AWS S3 para evitar custos iniciais)[cite: 8].

## Estrutura de Dados Base
- [cite_start]`users`: Gestão de identidade e roles[cite: 8].
- [cite_start]`providers_profile`: Dados profissionais e saldo retido[cite: 8].
- [cite_start]`service_requests`: Registro de chamados e status[cite: 8].
- [cite_start]`transactions`: Controle de transações e motor de Escrow[cite: 8].