# Debug delta — custom-predicates-polish, iteration 4

## Triage

**Neither test wrong nor code wrong for this feature — the Build check's failure is
unrelated to custom-predicates-polish.**

Gate report `testing/custom-predicates-polish-gate-4.md` attributed the `./gradlew build`
failure to a daemon crash and moved on. Re-running it (`--no-daemon`, full log at
`/tmp/build-full.log`) shows the daemon crash was a symptom of running the whole
`:formver.compiler-plugin:test` task under memory pressure on this host, not the real
failure: once it ran to completion, 23 tests failed for real, reproducibly (confirmed by
re-running two of them in isolation with the same errors).

Of the 23:
- 20 are `custom_predicates_{a2,b2,n2}_*` tests. None are in this feature's manifest
  (`testing/custom-predicates-polish-specs.md` covers only the six KI-01..05 programs).
  Bisected by checking out three points in this branch's history and running the same
  `--tests` filter in a scratch worktree:
  - At `d0bdec6a` (`[UNVERIFIED] Add Method A contract-property test cases`), the one test
    that existed at that commit (`a2_val_recursive`) already failed.
  - At `74f837a4` (the commit immediately before the Tester wrote the KI-01..05 manifest,
    `a6a7f586`), all 21 a2/b2/n2 tests that exist today already existed and 20 of them
    already failed with the same errors (e.g. `n2_n1_subexpr`:
    `kotlin.NotImplementedError: ... create new function object with counter, duplicable
    (requires toViper restructuring)`).
  These tests were added by three commits explicitly tagged `[UNVERIFIED]`
  (`d0bdec6a`, `6c3733b9`, `6c6b4b2a`) before any custom-predicates-polish work started, and
  never made to pass. None of this feature's commits (`6e581a2a` onward, including all
  three debugger iterations) touch them or their failure mode.
- 3 are `Expensive_verification > Algorithms` tests (`testZ_function`,
  `testQuick_sort_of_string`, `testMerge_sort_of_string`). No commit in this branch's
  history touches `testData/diagnostics/verification/algorithms`. Unrelated.

Per [[project-gradle-build-broken]] and [[project-test-gates]] (project memory):
`./gradlew build` running the full suite is not this feature's gate. The gate that matters —
`:formver.compiler-plugin:test --tests "*Custom_predicates_ki0[1-5]*"` — already passes, as
recorded in gate-4's Verifier/Regression/Negative rows.

## Fix

None applied. There is nothing in this feature's scope to fix: the KI-01..05 programs all
pass, and the 23 failures belong to test batches that predate this feature and were never
green.

## Verification

- `./gradlew :formver.compiler-plugin:test --tests "*Custom_predicates_ki0[1-5]*"`: PASS
  (already confirmed by Gate iteration 4).
- Baseline checks in scratch worktrees at `d0bdec6a` and `74f837a4`: same 20 a2/b2/n2
  failures present before this feature's work began.

## Remaining

Out of scope for this feature: 20 pre-existing `[UNVERIFIED]` a2/b2/n2 test failures and 3
unrelated Algorithm test failures. Recommend the operator either fix the `[UNVERIFIED]`
batches as their own effort, delete them if abandoned, or fix the Gate's Build check
definition to stop treating whole-suite `./gradlew build` as a per-feature gate — it will
keep reporting FAIL on every feature branch until one of those two things happens.
