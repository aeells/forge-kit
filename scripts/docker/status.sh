#!/usr/bin/env bash
set -euo pipefail

# docker-status.sh
# Show status of docker containers
# Usage: ./status.sh <service1> [service2] [service3] ...

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# ---- Dependencies -----------------------------------------------------------

require_command gum "Install with: brew install gum"
require_command jq "Install with: brew install jq"
require_command docker "Install Docker Desktop or use OrbStack"

# ---- Configuration ----------------------------------------------------------

SERVICES=("$@")

# ---- Helpers -----------------------------------------------------------------

# Function to get container ID
get_container_id() {
  local svc="$1"
  docker ps -a --filter "name=^/${svc}$" -q
}

# Function to get mapped ports (only shows ports mapped to host)
get_ports() {
  local cid="$1"
  local ports
  ports=$(docker inspect "$cid" 2>/dev/null \
    | jq -r '.[] | .NetworkSettings.Ports // {} | to_entries[]? | select(.value != null and (.value[0].HostPort != null)) | "\(.value[0].HostPort):\(.key | split("/")[0])"' \
    | paste -sd ", " -)
  [[ -z "$ports" ]] && ports="-"
  echo "$ports"
}

# ---- Main --------------------------------------------------------------------

# Check if any services were provided
if [[ ${#SERVICES[@]} -eq 0 ]]; then
  gum style --foreground 212 --bold "Docker Status"
  echo ""
  echo "Usage: $0 <service1> [service2] [service3] ..."
  echo "Example: $0 localstack jaeger"
  exit 1
fi

echo ""
gum style --foreground 212 --bold "Docker Container Status"
echo ""
gum style --foreground 240 --bold "SERVICE      | STATE      | PORTS"
echo ""

for s in "${SERVICES[@]}"; do
  CID=$(get_container_id "$s")
  if [[ -n "$CID" ]]; then
    STATE=$(docker inspect "$CID" --format '{{.State.Status}}' 2>/dev/null || echo "unknown")
    PORTS=$(get_ports "$CID")
    
    if [[ "$STATE" == "running" ]]; then
      styled_state=$(gum style --foreground 10 "$STATE")
    else
      styled_state=$(gum style --foreground 11 "$STATE")
    fi
  else
    STATE="not created"
    PORTS="-"
    styled_state=$(gum style --foreground 9 "$STATE")
  fi

  # Use printf with fixed widths, accounting for ANSI codes in styled_state
  service_name=$(printf "%-12s" "$s")
  state_padding=$(printf "%*s" $((10 - ${#STATE})) "")
  ports_padding=$(printf "%-30s" "$PORTS")
  
  echo -n "$service_name | "
  echo -n "$styled_state"
  echo -n "$state_padding | "
  echo "$ports_padding"
done

echo ""
