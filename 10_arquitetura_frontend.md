# 10. Arquitetura do Front-end (React Native + Expo)

## 1. Stack Tecnológica
- [cite_start]**Framework:** React Native via Expo (EAS Build)[cite: 187].
- **Linguagem:** TypeScript (para tipagem rigorosa dos contratos do Swagger).
- [cite_start]**Gerenciamento de Estado:** **Zustand** (fluxos de checkout e pedidos) e **Context API** (autenticação)[cite: 228].
- [cite_start]**Navegação:** React Navigation (Stacks e Tabs)[cite: 230].
- **Estilização:** NativeWind (Tailwind CSS para React Native) para agilidade visual.

## 2. Estrutura de Pastas (Padrão Atômico)
- [cite_start]`src/components`: Componentes reutilizáveis (Botões, Inputs, Cards)[cite: 229].
- `src/screens`: Telas principais (Home, Login, Perfil).
- `src/services`: Integração com a API via Axios.
- `src/hooks`: Lógica de geolocalização e chamadas assíncronas.

## 3. Diretrizes de Integração
- **Base URL:** Deve apontar para o IP do seu host Linux (não use `localhost` no mobile).
- [cite_start]**Tipagem:** Todos os retornos da API devem seguir os Records (DTOs) definidos no Back-end[cite: 223].