#!/usr/bin/env bash
set -euo pipefail

export HOME=$(echo ~)
cd "${WORKSPACE_DIR:?WORKSPACE_DIR must be set}/pipeline-ui"

npm run generate:clean-client-code
npm run test:coverage-pipeline
