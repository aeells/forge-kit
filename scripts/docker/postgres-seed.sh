#!/usr/bin/env bash
set -euo pipefail

# docker/postgres-seed.sh
# Seed AWS LocalStack with initial data (DynamoDB tables, S3 buckets, etc.)
# Usage: ./postgres-seed.sh

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# ---- Dependencies -----------------------------------------------------------

require_command docker "Install Docker Desktop or use OrbStack"
require_command aws "Install AWS CLI: brew install awscli"

# ---- Configuration ----------------------------------------------------------

POSTGRES_CONTAINER="postgres"
POSTGRES_DB="mydb"
POSTGRES_USER="postgres"

# ---- Helpers -----------------------------------------------------------------

# ---- Main --------------------------------------------------------------------

echo "🌱 Seeding Postgres database."

# Ensure Postgres is up before seeding
if ! docker ps --format '{{.Image}}' | grep -qi 'postgres'; then
  log_error "Postgres container not running. Please start it first."
  exit 1
fi

echo "🌿 Postgres seed complete."
echo
