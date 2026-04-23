# Marketplace Ceará

Marketplace Ceará é uma plataforma inovadora projetada para conectar prestadores de serviços a clientes, oferecendo uma experiência simples, segura e eficiente. O sistema lida com identidade, descoberta de profissionais por geolocalização, solicitações de serviços multimídia, pagamentos via Escrow e sistema de reputação.

## 🚀 Tecnologias Utilizadas

A arquitetura do projeto foi desenvolvida focando em performance, escalabilidade e facilidade de manutenção:

*   **Backend:** Java 21 com Spring Boot 3.x, utilizando Virtual Threads.
*   **Frontend Mobile:** React Native com Expo.
*   **Banco de Dados:** PostgreSQL para persistência de dados.
*   **Armazenamento Local:** MinIO (utilizado como alternativa local ao AWS S3 para gestão de arquivos multimídia).
*   **Infraestrutura:** Docker e Docker Compose nativos em Linux.

## 📁 Estrutura do Projeto

O repositório é configurado como um monorepo, contendo tanto o frontend quanto o backend da aplicação:

*   `/backend` - Código-fonte da API (Java/Spring Boot).
*   `/mobile` - Código-fonte do aplicativo (React Native/Expo).
*   `/docker-compose.yml` - Definição dos containers de infraestrutura.
*   `start-dev.sh` e `start-all.sh` - Scripts de automação para iniciar os serviços de banco de dados e armazenamento.

## ⚙️ Pré-requisitos

Antes de executar a aplicação localmente, certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

*   [Docker](https://docs.docker.com/get-docker/) e [Docker Compose](https://docs.docker.com/compose/install/)
*   [Java 21](https://jdk.java.net/21/) (com Maven instalado ou via wrapper do projeto)
*   [Node.js](https://nodejs.org/) (versão LTS recomendada) e [Yarn/NPM](https://www.npmjs.com/)
*   [Expo CLI](https://docs.expo.dev/get-started/installation/)

## 🏃 Como Executar o Projeto Localmente

### 1. Iniciar a Infraestrutura (PostgreSQL e MinIO)
O projeto conta com um script automatizado que valida pré-requisitos, sobe os containers do banco de dados e storage, e também já cria o bucket `marketplace-files` no MinIO.

```bash
chmod +x start-dev.sh
./start-dev.sh
```

A infraestrutura estará disponível em:
*   **PostgreSQL:** `localhost:5432` (DB: `marketplace_ceara` | User: `marketplace`)
*   **MinIO Console:** `http://localhost:9001` (User: `minioadmin` | Pass: `minioadmin123`)

### 2. Iniciar o Backend
Abra um novo terminal e inicie o servidor Spring Boot:

```bash
cd backend
mvn spring-boot:run
```

### 3. Iniciar o Frontend Mobile
Em outro terminal, inicie o servidor do Expo para o aplicativo:

```bash
cd mobile
npm install # ou yarn install
npx expo start
```
Após o build, você poderá escanear o QR Code gerado usando o aplicativo **Expo Go** em seu celular ou rodar em um emulador Android/iOS.

## 📚 Documentação Adicional

O projeto conta com várias documentações técnicas e de requisitos no diretório raiz:

*   `1_requisitos.md` - Épicos e Histórias de Usuário.
*   `2_regras.md` - Regras de negócio.
*   `3_arquitetura.md` - Detalhes arquiteturais.
*   `9_documentacao_api.md` - Contratos e fluxos da API.
*   E diversos outros arquivos `.md` cobrindo detalhes como identidade, pagamentos, avaliação, etc.
