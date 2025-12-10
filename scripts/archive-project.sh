#!/usr/bin/env bash

# Helper script to archive the parent project directory
# Uses the .archiveignore file in the scripts directory

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
ARCHIVE_SCRIPT="$SCRIPT_DIR/archive-with-txt.sh"
ARCHIVEIGNORE_FILE="$SCRIPT_DIR/.archiveignore"

if [ ! -f "$ARCHIVE_SCRIPT" ]; then
    echo "Error: archive-with-txt.sh not found in $SCRIPT_DIR"
    exit 1
fi

# Build ignore flags from .archiveignore file if it exists
IGNORE_FLAGS=()
if [ -f "$ARCHIVEIGNORE_FILE" ]; then
    while IFS= read -r line || [ -n "$line" ]; do
        # Skip empty lines and comments
        if [[ -n "$line" && ! "$line" =~ ^[[:space:]]*# ]]; then
            IGNORE_FLAGS+=(-i "$line")
        fi
    done < "$ARCHIVEIGNORE_FILE"
fi

# Run the archive script on the project root
exec "$ARCHIVE_SCRIPT" "${IGNORE_FLAGS[@]}" "$PROJECT_ROOT"

