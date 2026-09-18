#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [ ! -f .env ]; then echo "Missing .env. Copy .env.docker.example to .env and configure it."; exit 1; fi
docker compose pull mysql caddy
docker compose build --pull backend frontend
docker compose up -d
docker compose ps