#!/usr/bin/env bash
set -euo pipefail

# coverage-summary.sh
# Extract coverage metrics from Clover XML and generate GitHub Actions job summary
# Usage: ./coverage-summary.sh [clover-xml-path]
#
# Outputs GitHub Actions outputs:
#   - percentage: Coverage percentage
#   - covered: Number of lines covered
#   - total: Total number of lines

# ---- Configuration ----------------------------------------------------------

CLOVER_XML="${1:-target/site/clover/clover.xml}"
GITHUB_OUTPUT="${GITHUB_OUTPUT:-/dev/stdout}"
GITHUB_STEP_SUMMARY="${GITHUB_STEP_SUMMARY:-/dev/stdout}"

# ---- Helpers -----------------------------------------------------------------

generate_summary() {
  local percentage="$1"
  local covered="$2"
  local total="$3"

  cat >> "$GITHUB_STEP_SUMMARY" <<EOF
## 📊 Code Coverage Report

| Metric | Value |
|--------|-------|
| **Coverage** | ${percentage}% |
| **Lines Covered** | ${covered} / ${total} |

📄 **Download the full HTML report from the artifact below**

> Report generated at: $(date -u +'%Y-%m-%d %H:%M:%S UTC')
EOF
}

# ---- Main --------------------------------------------------------------------

# Initialize variables
covered=0
total=0
percentage="0"

# Extract coverage metrics from Clover XML
echo "🔍 Checking for Clover XML at: $CLOVER_XML" >&2
if [ -f "$CLOVER_XML" ]; then
  echo "✅ Clover XML file found" >&2
  echo "📏 File size: $(wc -c < "$CLOVER_XML") bytes" >&2
  
  # Check if xmllint is available
  if ! command -v xmllint &> /dev/null; then
    echo "❌ xmllint not found - installing libxml2-utils" >&2
    sudo apt-get update -qq && sudo apt-get install -y libxml2-utils >&2
  fi
  
  covered=$(xmllint --xpath "string(/coverage/project/metrics/@coveredstatements)" "$CLOVER_XML" 2>/dev/null || echo "0")
  total=$(xmllint --xpath "string(/coverage/project/metrics/@statements)" "$CLOVER_XML" 2>/dev/null || echo "0")
  
  echo "📊 Extracted values - covered: $covered, total: $total" >&2
  
  # Calculate percentage
  if [ "$total" -gt 0 ] 2>/dev/null; then
    percentage=$(awk "BEGIN {printf \"%.2f\", ($covered / $total) * 100}")
  else
    percentage="0"
    echo "⚠️  Warning: Total statements is 0 or invalid" >&2
    echo "🔍 First 20 lines of XML file:" >&2
    head -20 "$CLOVER_XML" >&2
  fi
else
  echo "❌ Clover XML file not found at: $CLOVER_XML" >&2
  echo "🔍 Checking for clover files in target/site/clover/:" >&2
  ls -la target/site/clover/ 2>&1 || echo "Directory does not exist" >&2
  echo "🔍 Checking for clover files in target/:" >&2
  find target -name "clover.xml" -type f 2>&1 | head -10 || true
fi

# Output GitHub Actions variables
echo "percentage=$percentage" >> "$GITHUB_OUTPUT"
echo "covered=$covered" >> "$GITHUB_OUTPUT"
echo "total=$total" >> "$GITHUB_OUTPUT"

# Generate summary
generate_summary "$percentage" "$covered" "$total"

