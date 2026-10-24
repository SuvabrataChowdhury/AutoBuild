# Bazel Build Setup

## Overview

Bazel is used as a unified build and test runner across services. Each service delegates
its actual build/test tooling (Maven, npm, etc.) to its native wrapper, while Bazel
provides caching and a consistent interface.

## Workspace Structure

```
AutoBuild/
├── .bazelrc
├── MODULE.bazel
├── pipeline/
│   ├── BUILD.bazel
│   └── run_tests.sh
└── pipeline-ui/
    ├── BUILD.bazel
    └── run_tests.sh
```

## Prerequisites

- [Bazel](https://bazel.build/install) installed
- `WORKSPACE_DIR` exported in your shell profile pointing to the repo root:

  ```sh
  export WORKSPACE_DIR=/path/to/AutoBuild
  ```

  This is required because Bazel runs tests in a sandbox and needs to know the real
  workspace path to invoke native build tools (mvnw, npm, etc.).

## Running Tests

```sh
# Run pipeline (Java/Maven) tests
bazel test //pipeline:test

# Run pipeline-ui (Node/npm) tests
bazel test //pipeline-ui:test
```

The `--test_env=WORKSPACE_DIR` flag is automatically applied to all `bazel test`
invocations via `.bazelrc`, so no extra flags are needed as long as `WORKSPACE_DIR`
is set in your shell.

## Caching

Bazel caches test results based on input file changes. If no source files or `pom.xml`
have changed since the last run, the result is served from cache:

```
//pipeline:test    (cached) PASSED in 13.5s
```

To force a re-run regardless of cache:

```sh
bazel test //pipeline:test --cache_test_results=no
```

## Adding Tests for a New Service

1. Create a `run_tests.sh` in the service directory:

   ```sh
   #!/usr/bin/env bash
   set -euo pipefail
   export HOME=$(echo ~)
   cd "${WORKSPACE_DIR:?WORKSPACE_DIR must be set}/<service-dir>"
   # run your tests, e.g. npm test or ./mvnw test
   ```

2. Make it executable: `chmod +x run_tests.sh`

3. Add a `BUILD.bazel` in the service directory:

   ```starlark
   load("@rules_shell//shell:sh_test.bzl", "sh_test")

   sh_test(
       name = "test",
       srcs = ["run_tests.sh"],
       data = glob(["src/**", "pom.xml"], exclude = ["target/**"]),
       tags = ["manual", "local"],
   )
   ```

## MODULE.bazel Dependencies

```starlark
bazel_dep(name = "rules_java", version = "9.9.0")
bazel_dep(name = "rules_shell", version = "0.6.1")
```
