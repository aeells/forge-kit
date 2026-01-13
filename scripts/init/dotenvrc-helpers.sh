#!/usr/bin/env bash
set -euo pipefail

# Shared helper functions for dotenvrc initialization scripts
# This file is sourced by other scripts, not executed directly

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# ---- Helpers -----------------------------------------------------------------

# Updates a key-value pair in .envrc file
# Args:
#   $1: key name
#   $2: value
#   $ENVRC_FILE: path to .envrc file (must be set by caller)
update_envrc_key() {
    local key=$1
    local value=$2

    if [[ -z "$ENVRC_FILE" ]]; then
        log_error "ENVRC_FILE must be set before calling update_envrc_key"
        return 1
    fi

    if grep -qE "^export ${key}=" "$ENVRC_FILE" 2>/dev/null; then
        # Update existing key in place
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s|^export ${key}=.*|export ${key}=\"${value}\"|" "$ENVRC_FILE"
        else
            sed -i "s|^export ${key}=.*|export ${key}=\"${value}\"|" "$ENVRC_FILE"
        fi
    else
        # Append new key (ensure file ends with newline)
        if [[ -s "$ENVRC_FILE" ]]; then
            local last_byte=$(tail -c 1 "$ENVRC_FILE" | od -An -tu1 | tr -d ' \n')
            if [[ "$last_byte" != "10" ]] && [[ -n "$last_byte" ]]; then
                printf '\n' >> "$ENVRC_FILE"
            fi
        fi
        printf 'export %s="%s"\n' "$key" "$value" >> "$ENVRC_FILE"
    fi
}

# Generates a base64-encoded encryption key from a raw key using jbang
# Args:
#   $1: raw encryption key
# Returns: base64-encoded key on stdout, or returns 1 on error
get_base64_encoded_key() {
    local raw_key=$1

    jbang https://raw.githubusercontent.com/smallrye/smallrye-config/main/documentation/src/main/docs/config/secret-handlers/encryptor.java \
        -s="dummy" \
        -k="$raw_key" \
        | grep '^smallrye.config.secret-handler.aes-gcm-nopadding.encryption-key=' \
        | cut -d'=' -f2
}

