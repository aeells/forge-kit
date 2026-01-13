#!/usr/bin/env bash
# Common utilities for all scripts in the forge-platform project
#
# This library provides standardized functions for:
# - Dependency checking
# - Logging
# - Path utilities
#
# Usage:
#   source "$(dirname "${BASH_SOURCE[0]}")/../lib/common.sh"

# ---- Dependency Checking -----------------------------------------------------

# Require a command to be available
# Args:
#   $1: command name (e.g., "gum", "jq")
#   $2: install hint (optional, e.g., "Install with: brew install gum")
# Exit: 1 if command not found
require_command() {
  local cmd="$1"
  local install_hint="${2:-}"
  
  if ! command -v "$cmd" &> /dev/null; then
    gum log --level error "$cmd is required but not installed."
    if [[ -n "$install_hint" ]]; then
      echo "$install_hint" >&2
    fi
    exit 1
  fi
}

# Require multiple commands to be available
# Args:
#   $@: command names
# Exit: 1 if any command not found (reports all missing commands)
require_commands() {
  local missing=()
  local cmd
  
  for cmd in "$@"; do
    if ! command -v "$cmd" &> /dev/null; then
      missing+=("$cmd")
    fi
  done
  
  if [[ ${#missing[@]} -gt 0 ]]; then
    gum log --level error "The following commands are required but not installed: ${missing[*]}"
    exit 1
  fi
}

# ---- Logging ------------------------------------------------------------------

# Log an error message
# Args:
#   $1: error message
log_error() {
  gum log --level error "$1"
}

# Log a warning message
# Args:
#   $1: warning message
log_warn() {
  gum log --level warn "$1"
}

# Log an info message
# Args:
#   $1: info message
log_info() {
  gum log --level info "$1"
}

# ---- Path Utilities ----------------------------------------------------------

# Get the absolute path to the project root
# Requires SCRIPT_DIR to be set by the calling script
# Returns: absolute path to project root (two levels up from script location)
# Example: script in scripts/docker/status.sh -> project root
get_project_root() {
  if [[ -z "${SCRIPT_DIR:-}" ]]; then
    echo "Error: SCRIPT_DIR must be set before calling get_project_root()" >&2
    exit 1
  fi
  echo "$(cd "$SCRIPT_DIR/../.." && pwd)"
}

