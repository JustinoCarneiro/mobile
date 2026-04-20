#!/usr/bin/env bash
# =============================================================================
# start-dev.sh — Marketplace Ceará
# Sobe os containers Docker e valida se os serviços estão prontos.
# =============================================================================

set -euo pipefail

# --- Cores para output ---
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

COMPOSE_FILE="$(dirname "$0")/docker-compose.yml"

log_info()    { echo -e "${CYAN}[INFO]${NC}  $1"; }
log_ok()      { echo -e "${GREEN}[OK]${NC}    $1"; }
log_warn()    { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }

# =============================================================================
# 1. Verificar pré-requisitos
# =============================================================================
log_info "Verificando pré-requisitos..."

if ! command -v docker &>/dev/null; then
    log_error "Docker não encontrado. Instale o Docker antes de continuar."
    exit 1
fi

if ! docker compose version &>/dev/null; then
    log_error "Docker Compose plugin não encontrado."
    exit 1
fi

log_ok "Docker $(docker --version | awk '{print $3}' | tr -d ',')"
log_ok "Docker Compose $(docker compose version --short)"

# =============================================================================
# 2. Subir os containers
# =============================================================================
echo ""
log_info "Subindo containers..."
docker compose -f "$COMPOSE_FILE" up -d --remove-orphans

# =============================================================================
# 3. Aguardar PostgreSQL
# =============================================================================
echo ""
log_info "Aguardando PostgreSQL ficar pronto..."

RETRIES=30
SLEEP_SEC=2

for i in $(seq 1 $RETRIES); do
    STATUS=$(docker inspect --format='{{.State.Health.Status}}' marketplace-postgres 2>/dev/null || echo "não encontrado")

    if [ "$STATUS" = "healthy" ]; then
        log_ok "PostgreSQL pronto! (tentativa $i)"
        break
    fi

    if [ "$i" -eq "$RETRIES" ]; then
        log_error "PostgreSQL não respondeu após $((RETRIES * SLEEP_SEC))s. Verifique os logs:"
        docker compose -f "$COMPOSE_FILE" logs postgres
        exit 1
    fi

    log_warn "PostgreSQL: '$STATUS' — aguardando... ($i/$RETRIES)"
    sleep $SLEEP_SEC
done

# =============================================================================
# 4. Aguardar MinIO
# =============================================================================
echo ""
log_info "Aguardando MinIO ficar pronto..."

for i in $(seq 1 $RETRIES); do
    STATUS=$(docker inspect --format='{{.State.Health.Status}}' marketplace-minio 2>/dev/null || echo "não encontrado")

    if [ "$STATUS" = "healthy" ]; then
        log_ok "MinIO pronto! (tentativa $i)"
        break
    fi

    if [ "$i" -eq "$RETRIES" ]; then
        log_error "MinIO não respondeu após $((RETRIES * SLEEP_SEC))s. Verifique os logs:"
        docker compose -f "$COMPOSE_FILE" logs minio
        exit 1
    fi

    log_warn "MinIO: '$STATUS' — aguardando... ($i/$RETRIES)"
    sleep $SLEEP_SEC
done

# =============================================================================
# 5. Criar bucket padrão no MinIO (se ainda não existe)
# =============================================================================
echo ""
log_info "Verificando bucket 'marketplace-files' no MinIO..."

# Criar script temporário dentro do container para evitar problemas de escaping
docker exec marketplace-minio sh << 'MINIO_SCRIPT'
/usr/bin/mc alias set local http://localhost:9000 minioadmin minioadmin123 --quiet 2>/dev/null
/usr/bin/mc mb --ignore-existing local/marketplace-files 2>/dev/null
MINIO_SCRIPT

MC_EXIT=$?
if [ $MC_EXIT -eq 0 ]; then
    log_ok "Bucket 'marketplace-files' pronto."
else
    log_warn "Não foi possível criar o bucket automaticamente."
    log_warn "Crie manualmente em: http://localhost:9001 (user: minioadmin / senha: minioadmin123)"
fi

# =============================================================================
# 6. Status final
# =============================================================================
echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${GREEN}  ✅  Ambiente de desenvolvimento pronto!${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
echo -e "  ${CYAN}PostgreSQL:${NC}  localhost:5432"
echo -e "             DB=${GREEN}marketplace_ceara${NC}  user=${GREEN}marketplace${NC}"
echo ""
echo -e "  ${CYAN}MinIO API:${NC}   http://localhost:9000"
echo -e "  ${CYAN}MinIO Console:${NC} http://localhost:9001"
echo -e "             user=${GREEN}minioadmin${NC}  senha=${GREEN}minioadmin123${NC}"
echo ""
echo -e "  ${CYAN}Backend:${NC}     cd backend && mvn spring-boot:run"
echo ""
docker compose -f "$COMPOSE_FILE" ps
