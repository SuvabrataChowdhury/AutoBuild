#!/usr/bin/env bash
set -euo pipefail

export HOME=$(echo ~)
cd "${WORKSPACE_DIR:?WORKSPACE_DIR must be set}/pipeline"
./mvnw --batch-mode test -Pintegration-test
