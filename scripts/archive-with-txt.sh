#!/usr/bin/env bash

# Script to recursively archive a directory as a zip file,
# renaming files to .txt extension if they don't already end in .txt
# Supports ignore patterns via -i/--ignore flags or .archiveignore file

set -e

usage() {
    cat << EOF
Usage: $0 [OPTIONS] <directory>

Options:
    -i, --ignore PATTERN    Ignore files/directories matching PATTERN (glob)
                            Can be specified multiple times
    -o, --output DIR        Output directory for the zip file (default: ./output)
    -v, --verbose           Show verbose output (what's being ignored)
    -h, --help              Show this help message

Examples:
    $0 src/
    $0 -i "target/*" -i "logs/*" src/
    $0 -i "*.log" -i "*.class" src/

Ignore patterns use glob matching (e.g., "target/*", "*.log", "**/node_modules")
EOF
    exit 1
}

IGNORE_PATTERNS=()
VERBOSE=false
OUTPUT_DIR=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -i|--ignore)
            if [ -z "$2" ]; then
                echo "Error: -i/--ignore requires a pattern"
                exit 1
            fi
            IGNORE_PATTERNS+=("$2")
            shift 2
            ;;
        -o|--output)
            if [ -z "$2" ]; then
                echo "Error: -o/--output requires a directory path"
                exit 1
            fi
            OUTPUT_DIR="$2"
            shift 2
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -h|--help)
            usage
            ;;
        -*)
            echo "Error: Unknown option $1"
            usage
            ;;
        *)
            if [ -z "$SOURCE_DIR" ]; then
                SOURCE_DIR="$1"
            else
                echo "Error: Multiple directories specified"
                usage
            fi
            shift
            ;;
    esac
done

if [ -z "$SOURCE_DIR" ]; then
    echo "Error: Directory argument required"
    usage
fi

if [ ! -d "$SOURCE_DIR" ]; then
    echo "Error: '$SOURCE_DIR' is not a directory"
    exit 1
fi

# Check for .archiveignore file in source directory
ARCHIVEIGNORE_FILE="$SOURCE_DIR/.archiveignore"
if [ -f "$ARCHIVEIGNORE_FILE" ]; then
    echo "Reading ignore patterns from .archiveignore..."
    while IFS= read -r line || [ -n "$line" ]; do
        # Skip empty lines and comments
        if [[ -n "$line" && ! "$line" =~ ^[[:space:]]*# ]]; then
            IGNORE_PATTERNS+=("$line")
        fi
    done < "$ARCHIVEIGNORE_FILE"
fi

# Get absolute path and base name
SOURCE_DIR=$(cd "$SOURCE_DIR" && pwd)
BASE_NAME=$(basename "$SOURCE_DIR")
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
ORIGINAL_DIR=$(pwd)

# Set output directory (default to ./output if not specified)
if [ -z "$OUTPUT_DIR" ]; then
    OUTPUT_DIR="${ORIGINAL_DIR}/output"
else
    # Convert to absolute path
    # Handle both relative and absolute paths
    if [[ "$OUTPUT_DIR" != /* ]]; then
        # Relative path - resolve from current directory
        # Normalize by removing leading ./ if present
        OUTPUT_DIR="${OUTPUT_DIR#./}"
        OUTPUT_DIR="${ORIGINAL_DIR}/${OUTPUT_DIR}"
    fi
    # Absolute paths are used as-is
    # Try to normalize the path (remove .. and . components) if parent exists
    PARENT_DIR=$(dirname "$OUTPUT_DIR")
    if [ -d "$PARENT_DIR" ]; then
        OUTPUT_DIR=$(cd "$PARENT_DIR" && pwd)/$(basename "$OUTPUT_DIR")
    fi
fi

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

OUTPUT_ZIP="${OUTPUT_DIR}/${BASE_NAME}_${TIMESTAMP}.zip"
TEMP_DIR=$(mktemp -d)

# Cleanup function
cleanup() {
    if [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
    fi
}
trap cleanup EXIT

# Check if a path should be ignored
should_ignore() {
    local path="$1"
    
    # If no ignore patterns, don't ignore anything
    if [ ${#IGNORE_PATTERNS[@]} -eq 0 ]; then
        return 1
    fi
    
    # Calculate relative path from source directory
    local rel_path="${path#"$SOURCE_DIR"}"
    # Remove leading slash if present
    rel_path="${rel_path#/}"
    # If empty, it's the source directory itself
    if [ -z "$rel_path" ]; then
        return 1  # Don't ignore the root
    fi
    
    # Get just the basename for simple name matching
    local basename_path
    basename_path=$(basename "$path")
    
    for pattern in "${IGNORE_PATTERNS[@]}"; do
        # Remove trailing slash from pattern if present
        local clean_pattern="${pattern%/}"
        
        # Check if basename matches (for simple patterns like "target")
        if [[ "$basename_path" == "$clean_pattern" ]]; then
            return 0  # Should ignore
        fi
        
        # Check if the relative path matches the pattern (bash glob matching)
        if [[ "$rel_path" == "$clean_pattern" ]]; then
            return 0  # Should ignore
        fi
        
        # Check if any component of the path matches (including parent directories)
        # Split path by / and check each component
        IFS='/' read -ra PARTS <<< "$rel_path"
        for part in "${PARTS[@]}"; do
            if [[ "$part" == "$clean_pattern" ]]; then
                return 0  # Should ignore (parent directory matches)
            fi
        done
        
        # Check if pattern matches as a suffix (for patterns like "*/target" or "**/logs")
        if [[ "$rel_path" == *"/$clean_pattern" ]] || [[ "$rel_path" == "$clean_pattern" ]]; then
            return 0  # Should ignore
        fi
        
        # Check if path starts with pattern (for patterns like "target/*")
        if [[ "$rel_path" == "$clean_pattern"/* ]]; then
            return 0  # Should ignore
        fi
        
        # Handle patterns like "target/*" - check if path starts with the base
        if [[ "$clean_pattern" == *"/*" ]]; then
            local base_pattern="${clean_pattern%%/*}"
            if [[ "$rel_path" == "$base_pattern"/* ]] || [[ "$rel_path" == "$base_pattern" ]]; then
                return 0  # Should ignore
            fi
        fi
    done
    
    return 1  # Should not ignore
}

echo "Archiving '$SOURCE_DIR' to '$OUTPUT_ZIP'..."
if [ ${#IGNORE_PATTERNS[@]} -gt 0 ]; then
    echo "Ignore patterns: ${IGNORE_PATTERNS[*]}"
fi

# Copy directory structure and rename files recursively
copy_and_rename() {
    local src="$1"
    local dest="$2"
    
    # Check if this path should be ignored (check early, before processing)
    if should_ignore "$src"; then
        if [ "$VERBOSE" = true ]; then
            echo "Ignoring: $src"
        fi
        return
    fi
    
    if [ -d "$src" ]; then
        # Create directory
        mkdir -p "$dest"
        # Recursively process contents (including hidden files)
        # Use shopt to enable dotglob temporarily, or handle both patterns
        shopt -s nullglob
        for item in "$src"/* "$src"/.*; do
            # Skip . and .. entries
            if [[ "$item" == "$src/." ]] || [[ "$item" == "$src/.." ]]; then
                continue
            fi
            # Check if glob matched anything
            if [ -e "$item" ] || [ -L "$item" ]; then
                local item_name
                item_name=$(basename "$item")
                copy_and_rename "$item" "$dest/$item_name"
            fi
        done
        shopt -u nullglob
    else
        # It's a file - rename if needed
        local final_dest="$dest"
        if [[ "$src" != *.txt ]]; then
            final_dest="${dest}.txt"
        fi
        # Create parent directory if needed
        mkdir -p "$(dirname "$final_dest")"
        cp "$src" "$final_dest"
    fi
}

# Copy and rename files
copy_and_rename "$SOURCE_DIR" "$TEMP_DIR/$BASE_NAME"

# Create zip file from temp directory
cd "$TEMP_DIR"
zip -r "$OUTPUT_ZIP" . > /dev/null 2>&1

echo "Archive created: $OUTPUT_ZIP"
echo "Files renamed to .txt extension (if not already .txt)"
