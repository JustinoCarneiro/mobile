# 4. Especificação: Módulo de Identidade (US01 e US02)

## 1. Escopo Funcional
- [cite_start]**US01:** Cadastro e Login de Clientes (E-mail/Senha)[cite: 10, 47, 196].
- [cite_start]**US02:** Cadastro de Prestadores com Perfil Profissional e Validação de CPF[cite: 197, 198].

## 2. Modelagem de Dados (Entidades JPA)
- [cite_start]**User:** `id` (UUID), `full_name`, `email` (unique), `password_hash`, `cpf` (unique), `role` (ENUM: CLIENT, PROVIDER)[cite: 51, 233, 242].
- [cite_start]**ProviderProfile:** `id` (UUID), `user_id` (OneToOne), `category`, `bio`, `rating_average` (0.0), `verification_status` (PENDING, APPROVED, REJECTED)[cite: 51, 108, 234].

## 3. Segurança e Performance
- [cite_start]**JWT:** Autenticação stateless com Claims de Role.
- [cite_start]**BCrypt:** Criptografia de senhas obrigatória[cite: 32, 46].
- [cite_start]**Concorrência:** Uso de **Virtual Threads** (Java 21) para o serviço de Background Check.

## 4. Lógica de Background Check (Simulada)
- [cite_start]O serviço `BackgroundCheckService` deve processar a validação de forma assíncrona[cite: 50, 198].
- [cite_start]Delay simulado de 5 segundos (I/O bloqueante) antes de atualizar o status do perfil para `APPROVED`[cite: 50, 254].