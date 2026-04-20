#!/bin/bash

# =============================================================================
# start-app.sh — Marketplace Ceará Mobile
# =============================================================================

set -e

echo "🚀 Iniciando Marketplace Ceará Mobile..."

# 1. Garantir que as dependências estejam instaladas
if [ ! -d "node_modules" ]; then
    echo "📦 node_modules não encontrado. Instalando..."
    npm install
fi

# 2. Verificar se o Tunnel foi solicitado
MODE="LAN (Rede Local)"
TUNNEL_FLAG=""

if [[ "$*" == *"--tunnel"* ]]; then
    MODE="Tunnel (Proxy Externo)"
    TUNNEL_FLAG="--tunnel"
fi

echo "📡 Modo: $MODE"

# 3. Rodar o Expo com limpeza de cache para evitar erros de Babel
npx expo start -c $TUNNEL_FLAG
