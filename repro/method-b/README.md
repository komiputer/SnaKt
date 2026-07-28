# Method B — community-case reproductions (sealed-interfaces)

Standalone reproductions run against the Kotlin version SnaKt pins (2.3.0). They use
`kotlinc.sh`, a minimal driver over `kotlin-compiler-embeddable-2.3.0` from the Gradle
cache, because no `kotlinc` CLI is installed.

    ./kotlinc.sh <src-dir> <extra-classpath|""> <out-dir>

- `case1/` — KT-4999219: sealed interface in a separate module gains a subtype.
- `case2/` — KT-7341456: sealed subject arriving as a Java platform type.
- `case3/` — KT-7301055: `when (val it = foo)` after an outer null-check.

Findings are in `artifacts/testing/sealed-interfaces-solver-B-1-iter-1.md` of the
pipeline run. The case-2 finding is also carried into the repo as the golden test
`formver.compiler-plugin/testData/diagnostics/verification/control_flow/exhaustive_when_platform_type.kt`.
