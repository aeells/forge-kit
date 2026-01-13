#!/usr/bin/env bash
set -euo pipefail

# template-helper.sh
# Helper script to create new bash scripts from template
# Usage: ./template-helper.sh -- <path/to/script-name>

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

# ---- Dependencies -----------------------------------------------------------

require_command sed "sed is usually pre-installed"

# ---- Configuration ----------------------------------------------------------

SCRIPT_PATH="${1:-}"

# ---- Main --------------------------------------------------------------------

if [ -z "$SCRIPT_PATH" ]; then
  echo "Usage: $0 -- <path/to/script-name>"
  echo "Example: $0 -- quarkus/port-manager"
  exit 1
fi

TEMPLATE_DIR="$SCRIPT_DIR"
TARGET_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
SCRIPT_NAME=$(basename "$SCRIPT_PATH")
SCRIPT_FILE="${TARGET_DIR}/${SCRIPT_PATH}.sh"

mkdir -p "$(dirname "$SCRIPT_FILE")"

if [ -f "$SCRIPT_FILE" ]; then
  log_error "Script already exists: $SCRIPT_FILE"
  exit 1
fi

# Copy template and update script name in comments
sed "s|script-name.sh|${SCRIPT_NAME}.sh|g; s|Description of what the script does|Add description here|g" \
  "$TEMPLATE_DIR/template.sh" > "$SCRIPT_FILE"

chmod +x "$SCRIPT_FILE"

echo "✅ Created new script: $SCRIPT_FILE"

