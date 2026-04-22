#!/usr/bin/env bash
# =============================================================================
# start-all.sh — Marketplace Ceará
# Sobe a infraestrutura (Docker), Backend (Spring Boot) e Mobile (Expo).
# =============================================================================

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${CYAN}=========================================================${NC}"
echo -e "${CYAN}   🚀 Iniciando todo o sistema Marketplace Ceará${NC}"
echo -e "${CYAN}=========================================================${NC}"

# Função para encerrar o backend quando o script terminar
cleanup() {
    echo -e "\n${YELLOW}Encerrando os serviços...${NC}"
    if [ -n "${BACKEND_PID:-}" ]; then
        echo -e "Parando Backend (PID ${BACKEND_PID})..."
        kill -TERM "$BACKEND_PID" 2>/dev/null || true
    fi
    echo -e "${GREEN}Serviços da aplicação parados.${NC}"
    echo -e "Os containers Docker continuam rodando em background."
    echo -e "Para parar os containers, use: ${CYAN}docker compose stop${NC}"
    exit 0
}

# Configura o trap para limpar ao receber sinais ou sair
trap cleanup SIGINT SIGTERM EXIT

# 1. Subir infraestrutura (PostgreSQL e MinIO) via start-dev.sh
echo -e "\n${GREEN}[1/3] Iniciando Infraestrutura (Docker)...${NC}"
if [ -f "./start-dev.sh" ]; then
    bash ./start-dev.sh
    if [ $? -ne 0 ]; then
        echo -e "${RED}Erro: Falha ao iniciar a infraestrutura Docker.${NC}"
        trap - SIGINT SIGTERM EXIT
        exit 1
    fi
else
    echo -e "${RED}Erro: Arquivo start-dev.sh não encontrado na raiz do projeto.${NC}"
    trap - SIGINT SIGTERM EXIT
    exit 1
fi

# 2. Iniciar o Backend
echo -e "\n${GREEN}[2/3] Iniciando Backend (Spring Boot) em background...${NC}"
if [ -d "backend" ]; then
    cd backend
    ./mvnw spring-boot:run &
    BACKEND_PID=$!
    cd ..
else
    echo -e "${RED}Erro: Diretório 'backend' não encontrado.${NC}"
    trap - SIGINT SIGTERM EXIT
    exit 1
fi

# Aguarda um pouco para não misturar imediatamente todos os logs de subida
echo -e "${CYAN}Aguardando 10 segundos para o Backend inicializar...${NC}"
sleep 10

# 3. Iniciar o Mobile (em foreground para interação)
echo -e "\n${GREEN}[3/3] Iniciando Mobile (Expo)...${NC}"
echo -e "${YELLOW}Dica:${NC} O Backend está rodando em segundo plano. Os logs poderão aparecer aqui."
echo -e "${YELLOW}Dica:${NC} Pressione [CTRL+C] para encerrar o Expo e o Backend juntos.\n"

if [ -d "mobile" ]; then
    cd mobile
    if [ -f "./start-app.sh" ]; then
        bash ./start-app.sh
    else
        echo -e "${RED}Erro: Arquivo start-app.sh não encontrado no diretório mobile.${NC}"
        trap - SIGINT SIGTERM EXIT
        exit 1
    fi
else
    echo -e "${RED}Erro: Diretório 'mobile' não encontrado.${NC}"
    trap - SIGINT SIGTERM EXIT
    exit 1
fi

# Quando o Mobile for encerrado normalmente (ou por erro), o EXIT trap vai rodar.
