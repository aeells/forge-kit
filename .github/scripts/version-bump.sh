#!/usr/bin/env bash
set -euo pipefail

# version-bump.sh
# Check and bump version using Commitizen
# Usage: ./version-bump.sh
#
# Outputs GitHub Actions outputs:
#   - current_version: The version before bumping
#   - bumped: "true" if version was bumped, "false" otherwise
#   - final_version: The final version (new version if bumped, current if not)

# ---- Configuration ----------------------------------------------------------

GITHUB_OUTPUT="${GITHUB_OUTPUT:-/dev/stdout}"

# ---- Main --------------------------------------------------------------------

# Configure git user
git config user.name "github-actions[bot]"
git config user.email "github-actions[bot]@users.noreply.github.com"

# Fetch latest tags and ensure we're on main
git fetch --tags
git checkout main
git pull --ff-only origin main

# Get current version from Maven
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "current_version=$CURRENT_VERSION" >> "$GITHUB_OUTPUT"
echo "Current version: $CURRENT_VERSION"

# Find the latest tag and extract version from it
# This ensures .cz.toml matches an existing tag, which cz bump requires
LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
if [ -n "$LATEST_TAG" ]; then
  # Extract version from tag (remove 'v' prefix if present)
  TAG_VERSION="${LATEST_TAG#v}"
  echo "Latest tag: $LATEST_TAG (version: $TAG_VERSION)"
  # Sync .cz.toml to the tag version so cz bump can find it
  sed -i "s/^version = \".*\"/version = \"$TAG_VERSION\"/" .cz.toml
else
  echo "No tags found, using Maven version"
  # Sync .cz.toml to current Maven version
  sed -i "s/^version = \".*\"/version = \"$CURRENT_VERSION\"/" .cz.toml
fi

# Let commitizen do the work - it will update changelog and .cz.toml
# If no bump is needed, it will exit with non-zero and we skip
BUMPED=false
if LEFTHOOK=0 cz bump --yes --changelog 2>&1; then
  # Read the new version that commitizen calculated
  NEW_VERSION=$(grep '^version = ' .cz.toml | sed 's/^version = "\(.*\)"/\1/')
  echo "Version bumped: $CURRENT_VERSION -> $NEW_VERSION"

  # Update Maven to match
  mvn versions:set -DnewVersion=$NEW_VERSION -DgenerateBackupPoms=false
  mvn versions:commit

  # Commit everything (changelog, .cz.toml, pom files)
  git add -A
  git commit -m "chore(release): bump version to $NEW_VERSION [skip ci]" --signoff --no-verify
  git tag -f v$NEW_VERSION

  echo "bumped=true" >> "$GITHUB_OUTPUT"
  echo "next_version=$NEW_VERSION" >> "$GITHUB_OUTPUT"
else
  echo "No version bump needed - no eligible commits found."
  echo "bumped=false" >> "$GITHUB_OUTPUT"
  echo "next_version=$CURRENT_VERSION" >> "$GITHUB_OUTPUT"
fi
