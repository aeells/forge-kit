#!/usr/bin/env bash
set -euo pipefail

# version-bump-summary.sh
# Generate GitHub Actions job summary for version bump status
# Usage: ./version-bump-summary.sh [current_version] [bumped] [next_version]
#
# Arguments can be omitted if provided via environment variables:
#   CURRENT_VERSION, BUMPED, NEXT_VERSION

# ---- Configuration ----------------------------------------------------------

CURRENT_VERSION="${CURRENT_VERSION:-${1:-}}"
BUMPED="${BUMPED:-${2:-false}}"
NEXT_VERSION="${NEXT_VERSION:-${3:-}}"
GITHUB_STEP_SUMMARY="${GITHUB_STEP_SUMMARY:-/dev/stdout}"

# ---- Helpers -----------------------------------------------------------------

generate_summary() {
  local current_version="$1"
  local bumped="$2"
  local next_version="$3"

  cat >> "$GITHUB_STEP_SUMMARY" <<EOF
## 📦 Version Bump Status

**Current Version:** \`${current_version}\`
EOF

  if [ "$bumped" = "true" ]; then
    cat >> "$GITHUB_STEP_SUMMARY" <<EOF

✅ **Version bumped:** \`${current_version}\` → \`${next_version}\`

Tag \`v${next_version}\` has been created and pushed.
EOF
  else
    cat >> "$GITHUB_STEP_SUMMARY" <<EOF

ℹ️ **No version bump needed** - no eligible commits found.
EOF
  fi
}

# ---- Main --------------------------------------------------------------------

generate_summary "$CURRENT_VERSION" "$BUMPED" "$NEXT_VERSION"
