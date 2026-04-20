# 📑 Documentação Geral da API — Marketplace Ceará (V1)

Este documento resume a arquitetura, segurança e todos os endpoints disponíveis no Back-end.

## 🚀 Stack Tecnológica
- **Linguagem:** Java 21 (com Virtual Threads ativadas).
- **Framework:** Spring Boot 3.x.
- **Banco de Dados:** PostgreSQL 16+.
- **Storage:** MinIO / AWS S3 (via SDK S3).
- **Segurança:** Spring Security + JWT (Stateless).

---

## 🔒 Segurança e Autenticação
A API utiliza **JWT (JSON Web Token)**. 
- **Header:** `Authorization: Bearer <seu_token>`
- **Roles:** `ROLE_CLIENT` (Cliente) e `ROLE_PROVIDER` (Prestador).

---

## 🛣️ Endpoints Disponíveis

### 1. Identidade e Acesso (`/api/v1/auth`)
| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/register` | Cadastro de Usuário (Cliente ou Prestador). | Público |
| `POST` | `/login` | Autenticação e emissão de Token JWT. | Público |

### 2. Descoberta (`/api/v1/providers`)
| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `GET` | `/nearby` | Busca geolocalizada por categoria e raio (Haversine). | JWT (Qualquer) |

### 3. Solicitações de Serviço (`/api/v1/services`)
| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/requests` | Abertura de chamado (Suporta Texto + Multipart/S3). | `ROLE_CLIENT` |
| `GET` | `/requests/{id}` | Detalhes de um chamado específico. | JWT (Envolvidos) |

### 4. Pagamentos e Escrow (`/api/v1/payments`)
| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/webhook` | Recebe notificações do Gateway e confirma o Escrow. | Público (Secured by IP) |

### 5. Reputação (`/api/v1/services/{id}/reviews`)
| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/{id}/reviews` | Avalia um serviço concluído (1-5 estrelas). | `ROLE_CLIENT` |

---

## 🏗️ Modelagem de Dados Principal
1. **Users:** Centraliza nome, e-mail, CPF, senha e Role.
2. **ProviderProfile:** Extensão de usuário com categoria, geolocalização e nota média.
3. **ServiceRequest:** Gerencia o ciclo de vida do serviço (PENDENTE -> CONCLUÍDO).
4. **Review:** Armazena as avaliações e comentários dos clientes.

---

## ⚡ Performance e Resiliência
- **Assincronismo:** Validação de CPF e recalculo de nota média utilizam **Virtual Threads** para não bloquear o usuário.
- **Atomicidade:** Todas as operações críticas (Escrow, Cadastro) utilizam `@Transactional`.