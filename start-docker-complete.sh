#!/usr/bin/env bash
# =============================================================================
# start-docker-complete.sh — Marketplace Ceará
# Sobe TODO o sistema usando Docker Compose.
# =============================================================================

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${CYAN}=========================================================${NC}"
echo -e "${CYAN}   🐳 Iniciando Marketplace Ceará 100% Docker${NC}"
echo -e "${CYAN}=========================================================${NC}"

# 1. Construir e subir containers
echo -e "\n${GREEN}[1/2] Construindo e subindo containers...${NC}"
docker compose up --build -d

if [ $? -ne 0 ]; then
    echo -e "${RED}Erro ao subir os containers.${NC}"
    exit 1
fi

# 2. Status
echo -e "\n${GREEN}[2/2] Verificando status...${NC}"
docker compose ps

echo -e "\n========================================================="
echo -e "  ✅  Sistema rodando em background!"
echo -e "========================================================="
echo -e "  Backend API:    http://localhost:8080"
echo -e "  MinIO Console:  http://localhost:9001"
echo -e "  PostgreSQL:     localhost:5433"
echo -e "  Mobile Packager: http://localhost:8081"
echo -e "========================================================="
echo -e "${YELLOW}Dica:${NC} Para ver os logs, use: ${CYAN}docker compose logs -f${NC}"
echo -e "${YELLOW}Dica:${NC} Para parar tudo, use: ${CYAN}docker compose down${NC}"
echo -e "========================================================="
