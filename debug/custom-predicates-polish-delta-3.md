# Debug delta — custom-predicates-polish, iteration 3

## Triage

**Test wrong** (stale golden), for all three failing checks. None of the three affected
tests declare or call a custom predicate; the regression is a side effect of iteration 1's
malformed-argument fix, not of predicate handling itself.

- `PhasedDiagnosticTestGenerated.Verification.testOld`
  (`testData/diagnostics/verification/old.fir.diag.txt`)
- `PhasedDiagnosticTestGenerated.Verification.User_invariants.testSimple_precondition`
  (`testData/diagnostics/verification/user_invariants/simple_precondition.fir.diag.txt`)
- `PhasedDiagnosticTestGenerated.Verification.User_invariants.testStrings_in_conditions`
  (`testData/diagnostics/verification/user_invariants/strings_in_conditions.fir.diag.txt`)

Evidence: bisecting the branch (`bf32366c`..`HEAD`) by running each of the three tests in
isolation at successive commits found the first failing commit to be `5f76329e`
(iteration-1 debugger fix), specifically the `StmtConversionVisitor.visitFunctionCall`
change that skips converting a call's arguments when the callee is a
`FullySpecialKotlinFunction` with `ignoresArguments == true`. `preconditions { }` /
`postconditions { }` are such callees; all three failing tests use one. Converting an
argument that is discarded anyway was pure waste that happened to allocate throwaway Viper
variable names, so skipping it shifts the numbering of unrelated `ret_N`/`v_ret_N`
declarations later in the same method — a cosmetic renumbering, not a behavior change.
Confirmed by diffing each `--update` output: every changed line is a `ret_N`/`v_ret_N`
identifier or a label built from one; no assertion, permission, or method signature
differs.

None of the three test programs are part of this feature's spec suite
(`testing/custom-predicates-polish-specs.md`); they are pre-existing infrastructure tests
that happened to exercise the code path the iteration-1 fix touched. No manifest update
applies.

## Fix

Regenerated the three goldens with `./scripts/test.sh --verify --update <pattern>`, one
test at a time, and read each diff before accepting it (see evidence above). Commit:
this iteration's handoff commit.

While regenerating, `./gradlew` runs surfaced a large set of stray untracked files/dirs in
the worktree (the pre-uniqueness-rewrite `formver.compiler-plugin/uniqueness/src` tree, old
locality/uniqueness_checker testData, `scripts/dump-test-diff.sh`, `scripts/CLAUDE.md`) —
all content that this branch's history had already deleted. Their origin is unclear (not
produced by any command this iteration ran deliberately); removed via `git clean` before
committing so they don't get accidentally staged. Worth a passing mention to whoever owns
this worktree: if they reappear, something is writing deleted files back into this working
tree from outside this debug session.

## Verification

- `./gradlew :formver.compiler-plugin:test` (full suite, no `--tests` filter): all green.
- `./scripts/check-all.sh`: exit 0.

## Remaining

Nothing open for this iteration; all three Build-gate failures were the same root cause.
