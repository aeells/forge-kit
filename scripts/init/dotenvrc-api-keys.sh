#!/usr/bin/env bash
set -euo pipefail

# Updates .envrc with required API keys if missing.
# Vars are exported to your current shell when direnv loads .envrc file.

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# Source shared helper functions
# shellcheck source=scripts/init/dotenvrc-helpers.sh
source "$SCRIPT_DIR/dotenvrc-helpers.sh"

# ---- Dependencies -----------------------------------------------------------

require_command gum "Install with: brew install gum"

# ---- Configuration ----------------------------------------------------------

ROOT_DIR="$SCRIPT_DIR/../.."
ENVRC_FILE="$ROOT_DIR/.envrc"

# ---- Preconditions -----------------------------------------------------------

# Ensure .envrc file exists and is writable
touch "$ENVRC_FILE"
if [[ ! -w "$ENVRC_FILE" ]]; then
    log_error "Cannot write to $ENVRC_FILE"
    exit 1
fi

# ---- Configuration -----------------------------------------------------------

ENVRC_KEYS=(
    "NVD_API_KEY"
    "OSS_INDEX_USER"
    "OSS_INDEX_API_KEY"
)

# ---- Main --------------------------------------------------------------------

gum style --border rounded --padding "1 2" --margin "1" --border-foreground "#305CDE" --width 60 \
    "Update .envrc with 3rd party API keys."

gum style --faint "📖 References:"
gum style --faint "  • OSS Index: https://ossindex.sonatype.org/doc/auth-required"
gum style --faint "  • NVD: https://nvd.nist.gov/developers/request-an-api-key"
gum style --faint "  • LinkedIn: https://www.linkedin.com/developers/"

# Process each API key
for key in "${ENVRC_KEYS[@]}"; do
    value="${!key}"  # indirect expansion to read the variable's value
    
    if [[ -z "$value" ]]; then
        new_value=$(gum input --prompt "Enter value for $key:" --password)
        
        if [[ -z "$new_value" ]]; then
            log_error "$key cannot be empty."
            exit 1
        fi
        
        update_envrc_key "$key" "$new_value"
        echo "✅ Updated $key in .envrc"
    fi
done
