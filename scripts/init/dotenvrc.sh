#!/usr/bin/env bash
set -euo pipefail

# Main script to initialize .envrc with all required environment variables.
# This script calls the specialized scripts for API keys and secrets.

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# ---- Dependencies -----------------------------------------------------------

require_command gum "Install with: brew install gum"
require_command jbang "Install with: brew install jbang"

# ---- Main --------------------------------------------------------------------

gum style --border rounded --padding "1 2" --margin "1" --border-foreground "#305CDE" --width 60 \
    "Initialize .envrc" \
    "" \
    "This will set up:" \
    "  • API keys (NVD, OSS Index, LinkedIn)" \
    "  • Encryption secrets (SmallRye Config)" \
    "" \
    "> direnv allow exports .envrc vars to current shell."

# Run API keys script
"$SCRIPT_DIR/dotenvrc-api-keys.sh"

echo
gum style --bold "✅ .envrc updated successfully. Run 'direnv allow' to apply changes."
