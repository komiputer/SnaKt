# custom-predicates run — rulings recovered after the artifact-root wipe

The shared artifact root `/home/silverbot/dev/.empty-sessions/empty-2wwyx31d/artifacts/` was emptied
between 15:20:18Z and 15:21:04Z on 2026-07-30 (parent mtime 17:20 local). Lost: `planner-state.md`
rev 5 (929 lines), the dispatcher state file rev2 (700+ lines), `ennio`'s rev1 (374 lines), and every
copied gate artefact. **No verified gate was lost, because there were no verified gates.**

## The finding the wipe produced

The artefact-preservation ruling was **correct in its reasoning and pointed at the wrong location.**
A worktree is unreadable to peers, so copying out was right. But the shared artifact root sits inside
a **reclaimable empty-session slot**, so it is not durable either. The evidence moved from a place
nobody else could read to a place that could be swept. **The property actually needed was durability,
and neither location had it. The only durable store available is a pushed git ref.** Every commit
survived; every non-committed artefact did not.

Corollary, from the same incident: **publish a checksum only for frozen artefacts, never for a living
state file.** A living file gets legitimately amended, and a conscientious hash check then raises a
false alarm caused by the very discipline meant to prevent one. For living files the path is the
identifier.

## Rulings, as recorded by `sacharissa` (dispatcher, iteration 1)

1. **137 is not the aggregation target.** The generated runner must be regenerated exactly **once,
   last**, after the final test file lands. It is a **merge gate owned by the aggregator, not a
   solver task.** Solvers leave it dirty, do not commit it, and do not revert anyone else's
   regeneration. Both directions of churn cost a commit.
2. **The counting identity**, confirmed independently on three branches. `grep -c 'runTest('` == count
   of `.kt` files is the **invariant pair**. `grep -c 'public void test'` (== runTest + the 20
   `testAllFilesPresentIn*` methods) is the **total gate count**. barr 119/139, soren 125/145,
   feature tree 137/157. `grep -c '@Test'` is off by more than double, because it also matches
   `@TestMetadata` and `@TestDataPath`; it self-refutes.
   **The invariant is a check against the object store, not against a post-build working tree** — two
   figures can both be true of different trees, and the defect is the missing name of the tree.
3. **`barr`'s controls are rejected with insufficient-permission on the predicate access.** The
   standing escalation does not fire. First direct evidence the controls discriminate at all — on
   access **presence**, which was never the doubtful half. **Content is still undiscriminated,
   because there is no `fold`.**
4. **New finding.** A predicate access is **consumed by the first forwarding call**: it is emitted as
   `FullPerm` and the call site exhales it with nothing returning it. **Forwardable exactly once, and
   unrecoverable afterwards because folding is unavailable.** Method B's verdict is "stated and
   forwarded once".
5. **Stopping a run that has not yet acquired the lock does not consume a re-run.** Nothing ran, so
   there is no result to re-run.
6. **An absent or empty test-results directory is the signature of a JVM death, not evidence of zero
   tests.**
7. **cwd drift** — fifth sighting, first to cost a resource. A queued run whose cwd is inside
   `testData` is invisible while waiting and fatal on acquire, because `./gradlew` is not there.
   **Put an absolute `cd` to the repo root inside the `flock` command.**

## Rulings from the Planner seat that were in `planner-state.md` rev 5

- **A gate counts as verified only when you hold the command line and its captured output.** Not a
  reported result, not a remembered green, not a manifest state.
- `:untilConversion` sets `CHECK_CONVERSION` and Z3 never runs. A conversion-only gate read as
  verification evidence cleared three consecutive gates before anyone caught it. **The gate for a new
  feature is FULL `:test`.**
- **A test count arrives with two commands or it is not a result**: the command that ran the tests and
  the command that extracted the number. Gradle prints no counts on a successful run, so any count
  attributed to a success log is misattributed.
- **When two readings of a number differ, prefer the one that fails loudly.** A false alarm costs a
  lock slot; a false green costs the deliverable. Every provenance instance in this run was someone
  choosing the quieter reading.
  - Live instance, mine: I claimed a FULL gate on the feature tree "correctly reports 136".
    **Retracted — the expectation is 157**, because `compileTestKotlin dependsOn(generateTests)` and
    `generateTests` declares `inputs.dir(testData)`, so the gate regenerates the runner against the
    on-disk files before compiling. **136 on that tree is the signature that the new cases were never
    wired in and none of them ran.** I had established those grounds myself, that morning, to refute
    a different alarm, and then accepted a claim they forbid.
- **A negative case is not satisfied by failing. It is satisfied by failing for the stated reason.
  Require the diagnostic text, not the exit status.**
- **The golden-update trap**: an update pass rewrites the test **source** as well as the goldens,
  inserting inline markers, so program and expectation are both bent to observed behaviour. No golden
  from an update run without a human-legible diff someone has read. Require a final non-update run
  green without having rewritten any golden.
- **The two-attempt re-run budget is structural, not hardening.** `flock` has no fairness guarantee,
  so the tail is unbounded in principle; **only a bound makes an unbounded wait reportable.**
- **Label every rule handed to a subordinate structural or hygiene, and resolve ambiguous cases as
  structural.** A hygiene rule mistaken for structural costs time; a structural rule mistaken for
  hygiene costs the result.
- **A fix must land where the error propagated, not only where it was noticed.** Three instances,
  including my own retraction reaching the planner state but not the document the dispatcher reads.
- **Over-retraction is its own defect class**: withdrawing a whole claim when one part of it failed.
  The tell — count the claims you made, then count the ones the correction addressed, and dispose of
  the remainder.
- Never `git stash` (`refs/stash` is one ref shared by every worktree). Never pattern-kill. Never
  `git init` the artifact root. Write state incrementally, not at handover.

## The headline finding — hold it precisely

Predicate accesses enter a program **only** at `Stmt.Inhale`. **`Stmt.Fold` has no constructor
anywhere in the plugin**, and that absence is **pre-existing upstream**, verified at `bf32366c`.
**`Exp.Unfolding` works and was inherited**, not built by this run. `Stmt.Unfold` has two live sites
that cannot fire, both behind `unfoldToAccess`, true only for a policy already diverted to `havoc`.

Consequence, **as inference and not proof**: a custom predicate can be **assumed and consumed but
never established**, so positive cases risk **passing vacuously**. **This is not unsoundness** —
Viper preconditions are assumptions by design. Keep that qualification attached; it is the difference
between a limitation and a bug.

## Surviving commits at the time of the wipe

`4be0c15a`, `e10bcf18`, `4ead163e`, `458cb00f`, `b0bf797a`, `af5c2652`, `819ba770` — all intact on
`origin`. Solver branches `origin/solve/solver-{a-1,b-1,n-1}`.

**Zero verified gates.** `complete/custom-predicates-step-4-iter-1.md` was never written.

## Late deltas, after the wipe

- `sacharissa` pushed the dispatcher-side recovery to `origin/recovery/dispatcher-state-iter1` at
  `f55349db`, file `DISPATCHER-INCIDENT-RECOVERY.md`. It is honestly labelled as the load-bearing
  subset, **not** as the 700-line state file, which was not reconstructable at its context.
- **`barr`'s target moved to 140 = 120 `.kt` + 20**, up from 139, because the single-use pin is a
  fourth file. Four b1 files: three forwarding once with their controls, one pinning the limit. All
  six stale goldens deleted — correct, since the programs changed and stale goldens would have diffed
  against the two-call shape. Runner dirty at 119, regenerating to 120.
- **`barr`'s own qualification, made unprompted, and it is the right one.** Its gate will report
  `BUILD SUCCESSFUL` with the single-use case's **failure encoded in a golden**. So the green means
  *everything behaves as recorded, including one recorded failure* — **not** *everything verifies*.
  **That distinction must travel with the number**, or a reader comparing greens across solvers draws
  the wrong conclusion. This is the golden-update trap in its legitimate form: the mechanism is
  correct here and the reading of it is what can fail.
- Time-sensitive at the moment of the wipe: `barr`'s chain was still running and its fresh artefacts
  had **nowhere durable to go** until someone told it the new destination. **Telling the live solvers
  that gate artefacts go to a pushed ref is the first instruction the next dispatcher seat owes.**
