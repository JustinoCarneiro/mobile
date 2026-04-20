# 0. Estratégia de Qualidade e Verificação (QA)

Este documento define os padrões obrigatórios de teste para o Marketplace Ceará. Toda implementação deve ser acompanhada por seus respectivos testes.

## 1. Back-end (Spring Boot 3.x / Java 21)
- **Unidade:** JUnit 5 e Mockito para serviços e regras de negócio.
- [cite_start]**Integração:** Testcontainers para validar persistência no PostgreSQL e storage no MinIO. [cite: 190, 258]
- [cite_start]**Concorrência:** Testes específicos para validar o comportamento das Virtual Threads em carga. [cite: 213]

## 2. Front-end (React Native / Expo)
- [cite_start]**Unidade/Componente:** Jest e React Testing Library (RTL). [cite: 187, 225]
- **Snapshot Testing:** Garantia de integridade visual contra alterações acidentais de CSS/Layout.
- **API Mocking:** Uso de MSW (Mock Service Worker) para isolar o desenvolvimento do front-end.
- **E2E (Ponta a Ponta):** Maestro para simular o fluxo real do usuário (ex: login completo até a Home).

## 3. Critérios de Blindagem
- [cite_start]**Execução:** Todos os testes devem rodar via terminal Linux (`mvn test` ou `npm test`). [cite: 259]
- **Automação:** Proibido código que dependa de interação manual para passar no teste.
- **Sintaxe:** Os testes devem seguir os padrões de design (Page Object ou similares) já estabelecidos.