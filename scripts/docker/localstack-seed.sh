#!/usr/bin/env bash
set -euo pipefail

# docker/localstack-seed.sh
# Seed AWS LocalStack with initial data (DynamoDB tables, S3 buckets, etc.)
# Usage: ./localstack-seed.sh

# ---- Imports ----------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/../lib/common.sh"

# ---- Dependencies -----------------------------------------------------------

require_command docker "Install Docker Desktop or use OrbStack"
require_command awslocal "Install with: pip install awscli-local or use LocalStack CLI"

# ---- Configuration ----------------------------------------------------------

# ---- Helpers -----------------------------------------------------------------

# dynamo
# s3
# ssm etc.

# ---- Main --------------------------------------------------------------------

echo "🌱 Seeding AWS LocalStack data."

# Ensure LocalStack is up before seeding
if ! docker ps --format '{{.Image}}' | grep -qi 'localstack'; then
  log_error "LocalStack container not running. Please start it first."
  exit 1
fi

echo "🌿 AWS LocalStack seed complete."
echo
