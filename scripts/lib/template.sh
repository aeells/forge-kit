#!/usr/bin/env bash
set -euo pipefail

# script-name.sh
# Description of what the script does
# Usage: ./script-name.sh [args]

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# ---- Dependencies -----------------------------------------------------------

require_command gum "Install with: brew install gum"
# Add other dependencies as needed

# ---- Configuration ----------------------------------------------------------

# Script-specific configuration variables

# ---- Helpers -----------------------------------------------------------------

# Helper functions specific to this script

# ---- Main --------------------------------------------------------------------

# Main script logic

