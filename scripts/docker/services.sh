#!/usr/bin/env bash
set -euo pipefail

# docker-services.sh
# Start or stop docker compose services
# Usage: ./services.sh start|stop <service>

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# ---- Dependencies -----------------------------------------------------------

require_command docker "Install Docker Desktop or use OrbStack"

# ---- Configuration ----------------------------------------------------------

ACTION="${1:-}"
SERVICE="${2:-}"

# ---- Main --------------------------------------------------------------------

if [[ -z "$SERVICE" ]]; then
  echo "Usage: $0 start|stop <service>"
  exit 1
fi

case "$ACTION" in
  start)
    CONTAINER_ID=$(docker compose ps -q "$SERVICE")
    if [ -n "$CONTAINER_ID" ]; then
      echo "🚀 Starting existing container for $SERVICE..."
      docker compose start "$SERVICE"
    else
      echo "🚀 No existing container found for $SERVICE, creating and starting..."
      docker compose up -d "$SERVICE"
    fi
    echo "✅ $SERVICE started."
    ;;
  stop)
    CONTAINER_ID=$(docker compose ps -q "$SERVICE")
    if [ -n "$CONTAINER_ID" ]; then
      echo "🛑 Stopping container for $SERVICE..."
      docker compose stop "$SERVICE"
      echo "✅ $SERVICE stopped."
    else
      echo "No running container found for $SERVICE."
    fi
    ;;
  *)
    echo "Unknown command: $1"
    echo "Usage: $0 start|stop <service>"
    exit 1
    ;;
esac
