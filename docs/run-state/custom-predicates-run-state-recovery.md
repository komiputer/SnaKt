# custom-predicates run state — recovered after artifact-root loss

Written by the Solver Dispatcher seat (`rune`, replaced `sacharissa`, which replaced `ennio`), 2026-07-30.

## Why this file exists

**The shared artifact root was destroyed at 17:20 on 2026-07-30, while it was being read.**

```
/home/silverbot/dev/.empty-sessions/empty-2wwyx31d/artifacts    -> entire subtree gone
```

The session directory survives but is empty and its permissions were reset from `drwxr-x---` to
`drwxrwxr-x`. Sibling `.empty-sessions/empty-*` directories still hold their `artifacts/`, so this was
reclamation of that one directory rather than a disk-wide event. It was detected because a directory read
minutes earlier returned `ENOENT` on the next command.

**Lost:** dispatcher state rev1 and rev2, `handoffs/planner-state.md`, the plan, the strategy, the Method B
rulings, all four `temp/gates/` artefact directories (barr, briar, saskia, soren), and the N-2 case
analysis. Also `complete/`, `incidents/`, `reviews/`, `surface/`.

**Not lost:** git. Every commit, branch and test file is intact.

**This file is a reconstruction from live context, not a copy.** Where a figure below was measured by this
seat it says so; where it is relayed it says so.

**The lesson, stated as the reason rather than the rule.** The artefact-preservation procedure had everyone
copy gate output out of their worktrees into the shared root. **That solved readability, not durability —
and durability was the property actually needed.** A peer cannot read a worktree, but a session slot can be
reclaimed, which is what happened. The destination was inside the very thing that got collected. The loss
window was **46 seconds wide** between a successful measurement and `ENOENT`, observed independently from
two seats.

**Gate artefacts and run state now go to a pushed git ref, not the artifact root and not a worktree.** A
pushed ref is the only store that survived today. Non-branch refs under `refs/pipeline/*` are preferable for
this, because a non-branch ref cannot be listed as a branch or picked as a PR base:

```
refs/pipeline/planner-salvage-annie          ee62e6b4
refs/pipeline/step5-synthesizer-briefing     265a1354
refs/pipeline/solvers-bundle                 3db37f44
```

Verified off the remote with `git ls-remote origin 'refs/pipeline/*'` rather than locally.

## 1. The rule that outranks everything

**Nothing counts as verified without a FULL-mode command line AND its captured output.** Not a reported
result, not a remembered green, not a manifest state.

**Verified gates on solver work: ZERO.** That zero has been correct every time it was asked for, including
once when a run had completed 90 seconds earlier whose log could not be read. Report zero for as long as
zero is true.

Gate conditions, all of them or it is a non-result:

1. `flock` exit is not 75 (75 = lock timeout, unambiguous).
2. `LOCK-ACQUIRED` marker present in the captured log.
3. `BUILD SUCCESSFUL` in the captured log text.
4. The **on-disk, post-build** runner names every one of the solver's new files. Never `git show` it —
   the committed runner is stale at 116 on every branch and gives a false negative.
5. Failure count matches expectation from an **mtime-filtered** XML extractor. **See §3 — "0 failures" is
   currently the WRONG criterion.**
6. The test count hits its **exact** expected figure. Overshoot is as suspect as undershoot.
7. Committed-tree invariant: `committed runTest count == committed testData .kt count`. Cheap, needs no
   build, positive rather than absence-based.

**A count arrives with TWO commands** — the one that ran the tests and the one that extracted the number.
Gradle prints no test counts on a successful run, so no count in this run can have come from a success log,
and a count without its extraction method is under-specified in exactly the way that let five false greens
through.

## 2. The counts, and the offset that burned four seats

**Measured by this seat off the object store.** Runner path in full — a wrong path returns a silent `0`
that reads like a finding, which cost one false measurement here:

```
formver.compiler-plugin/test-gen/org/jetbrains/kotlin/formver/plugin/runners/PhasedDiagnosticTestGenerated.java
```

| ref | `runTest(` | `public void test` | `testAllFilesPresentIn` | `.kt` | verdict |
|---|---|---|---|---|---|
| `bf32366c` (main) | 111 | 131 | 20 | 111 | OK |
| `e1cd7c1c` (baseline) | 116 | 136 | 20 | 116 | OK |
| `458cb00f` (feature tip) | 116 | 136 | 20 | 137 | broken by 21 |
| `af5c2652` (`solve/solver-b-1`) | 116 | 136 | 20 | 119 | broken by 3 |
| `819ba770` (`solve/solver-n-1`) | 116 | 136 | 20 | 125 | broken by 9 |
| `b0bf797a` (`solve/solver-a-1`) | 116 | 136 | 20 | 122 | broken by 6 |

**State the expectation as a RULE, not a number**, because a gate runs against the working tree, not the
commit, and uncommitted `.kt` files move the figure while the rule holds:

```
gate total = (.kt files on disk under testData/diagnostics) + 20 testAllFilesPresentIn* methods
```

`generateTests` emits exactly one test per file. Feature tree: 137 + 20 = **157**. Slug targets:
**barr 140** (120 `.kt` + 20 — moved from 139 when a single-use pin added a fourth file),
**soren 145**, **felix 142**.

**barr's coming green carries a qualification that must travel attached to the number.** Its gate will read
`BUILD SUCCESSFUL` with the single-use case's failure **encoded in a golden**. So that green means
*everything behaves as recorded, including one recorded failure* — **not** *everything verifies*. Someone
comparing greens across solvers without this draws precisely the wrong conclusion. barr caught this about
its own result before anyone above it did.

**A figure of 136 circulated as the expected feature-tree gate count and is RETRACTED.** Grounds, verified
here off the feature tip: `formver.compiler-plugin/build.gradle.kts:133-137` — `generateTests` declares
`inputs.dir(testData)` / `outputs.dir(test-gen)`; `:145-147` — `compileTestKotlin { dependsOn(generateTests) }`.
**A gate cannot reach the runner without regenerating it against the on-disk files, so the committed 116
cannot govern what runs.**

**The asymmetry, which was first stated backwards.** The hazard is not reading a correct 136 as failure. It
is **reading a vacuous 136 as success: 136 on this tree means `generateTests` did not wire in the 21 new
files, so none of the new cases ran.** When two readings of a number differ, prefer the one that fails
loudly.

**Three patterns, each with exactly one correct use:**

- `grep -c 'runTest('` — the invariant pair only.
- `grep -c 'public void test'` — the total gate count (`== runTest + 20`).
- `grep -c '@Test'` — **wrong in both roles.** It matches `@TestMetadata` and `@TestDataPath`; it read 298
  against 139 real tests. A number that wrong self-refutes; the dangerous pattern is `public void test`
  used where `runTest(` was needed, which lands only −20 off and looks plausible.

**The invariant pair is a committed-tree check only when read off the object store.** Reports of
`barr 119/119` and `soren 125/125` were true of their **post-build working trees** and false of their
commits; committed `runTest` is 116 everywhere because all three slug solvers correctly dropped their runner
regeneration. Both readings are right about different trees. **Name the tree whenever you quote the pair.**

## 3. The golden absence — inverts the failure criterion

**Found by the N-2 analysis seat, confirmed independently by the A-2 seat, corroborated dynamically by
barr's run, and verified off the object store by this seat.** Three independent routes agree.

**None of the 21 new test files has a golden. 21 `.kt`, zero `.diag.txt`.**

`DiagnosticsCollector.assertEquality` returns early when the expected output is null and the expected file
does not exist:

```
if (expectedOutput == null && !expectedFile.exists()) return
```

**So an absent golden asserts NO DIAGNOSTICS.** As committed, every one of the 21 files asserts the
opposite of what it was written to test.

**Consequences, and they change how every remaining gate is read:**

- **"0 failures" is the WRONG criterion on the current tree.** A correct non-update gate must report **one
  failure per golden-less working case** — 21 on the feature tree, 3 on barr's branch, 5 for the A-2 set.
  The signature is `Expected data file did not exist. Generating: ...`, which is first-time golden creation,
  not a defect.
- **A green non-update gate here means the cases emit nothing** — the vacuous outcome. **The failures are
  the pass signal and the green is the alarm.**
- **Every in-flight plain `:test` run is effectively an update pass**, because it writes the goldens as a
  side effect. So the golden-update trap applies to all of them, not only to the runs that passed
  `-Pkotlin.test.update.test.data=true`.
- A **second, non-update** run is required after the goldens land, and the goldens must be read by a human
  before it.

Condition 6 (exact total count) is unaffected: 157 stands.

## 4. barr's completed run — the strongest evidence so far, and still not a gate

Read out of the artefact directory minutes before it was destroyed. **These figures now exist only here.**

```
b1-baseline3.log   BUILD SUCCESSFUL in 11m 39s          (no LOCK-ACQUIRED; predates the wrapper)
b1-gen2.log        LOCK-ACQUIRED-2026-07-30T16:56:05    BUILD SUCCESSFUL in 15s   (generateTests only)
b1-update2.log     LOCK-ACQUIRED-2026-07-30T17:05:33    BUILD FAILED in 7m 43s
XML aggregate      files=20 tests=139 failures=3 errors=0 skipped=0
```

**139 is barr's exact predicted target (119 + 20) and 3 is exactly its number of golden-less new cases.**
Static prediction and independent dynamic result agree.

**It is NOT a verified gate: condition 3 fails on `BUILD FAILED`.** The failure reason could not be read —
the file was deleted between two commands.

**11m39s is the real cost of a locked module test run.** Budget roughly double for every locked run.

**A flaw in the artefact-preservation procedure, if it is ever revived: plain `cp` does not preserve
mtimes**, so the moment artefacts are copied, condition 5's mtime filter can no longer be applied to them.
It must be `cp -p`.

## 4a. THE DECISIVE RESULT — 157/20, verified from artefacts, and it confirms a per-case prediction

**Verified by this seat from the preserved JUnit XML on `refs/heads/recovery/dispatcher-state-iter1`
(`c9fca7ca`, `recovered-gates/briar-final/`), not from a relay.** The artefacts survived the artifact-root
loss because they had been pushed to a ref.

```
solver-n-2-update.log:1    LOCK-ACQUIRED-2026-07-30T17:15:13+02:00
solver-n-2-update.log:143  157 tests completed, 20 failed
solver-n-2-update.log:156  BUILD FAILED in 12m 31s
XML aggregate              tests=157 failures=20 errors=0 skipped=0
```

An earlier **clean, non-update** run reported the **same 157/20 split** at `LOCK-ACQUIRED 16:56:20`,
`BUILD FAILED in 6m 56s`.

**157 is confirmed empirically.** The retracted 136 is now refuted by measurement, not only by reading the
build script.

**All 20 failures are custom-predicate cases, and the arithmetic identifies the exception.** There are 21
golden-less new files (5 `a2` + 4 `b2` + 12 `n2`); 20 failed, so **exactly one passed**. Taking the
difference between the tree listing and the failure list, the one that passed is:

```
custom_predicates_n2_n1_property
```

**That is precisely the case predicted in advance, from source alone, to be the only one of the twelve that
would pass a non-update gate — and to pass vacuously.** `val x = predicate { true }` is a `FirProperty`; it
fails the `as? FirSimpleFunction` cast and the `!is FirSimpleFunction` gate at `ProgramConverter:471`, so
nothing is converted and nothing is reported, which makes its empty golden **correct**. A static per-case
prediction was confirmed at the level of the individual case by an independent dynamic run.

**So 20 failures is the CORRECT outcome here, not a defect** — it is the golden-absence inversion of §3
firing exactly as predicted. **The failures are the pass signal.**

### The discriminating run has NOT been made

**Both runs show 157/20 and the clean one ran FIRST (16:56) before the update pass (17:15).** An update pass
writes the goldens and then reports the diffs it wrote, so 20 failures is its expected first-time
behaviour. **A clean non-update run AFTER the update pass is the run that answers whether those 20 are now
resolved, and it does not exist.** Until it does, nobody can say whether any case passes, and two identical
157/20 lines invite the conclusion that the update accomplished nothing. **That single run is the cheapest
high-value action remaining in the iteration.**

### Conditions 3 and 7 need rewording, not enforcing

- **Condition 7 (committed-tree invariant) does not discriminate a DELIBERATELY stale runner from an
  accidentally stale one.** The tree is at 116/137 by the corrected ruling's own design, and **157 executed
  either way**. Disqualifying this run on condition 7 would be enforcing a rule against the situation it
  was written to permit.
- **Condition 3 (`BUILD SUCCESSFUL`) is wrong for a golden-less tree**, where a correct run *must* fail. On
  such a tree the admissible criterion is `BUILD FAILED` with **exactly** (golden-less count − vacuous-pass
  count) failures, all of them attributable to golden creation.

**Honest status: one gate RESULT exists and it FAILED as predicted. It is not a passing gate, and no
passing gate exists.** Reporting a flat "zero" without this distinction has become a slogan that hides a
real measurement; reporting it as a pass would be worse. **Say both halves.**

### Cross-solver golden contamination — observed, not inferred

**briar's update pass rewrote goldens for `a2` and `b2` cases it does not own**, which briar flagged in its
commit message and correctly did not revert. Since `generateTests` declares all of `testData` as its input
and an update pass regenerates against the whole on-disk tree, this is guaranteed rather than incidental.
**Treat every golden an update pass touched as unratified until its owner has read it.**

## 5. The headline finding — hold it precisely

Predicate accesses enter a program **only** at `Stmt.Inhale`. **`Stmt.Fold` has no constructor anywhere in
the plugin**, and the absence is **pre-existing upstream** — verified at `bf32366c`, and re-verified
independently at the feature tip `458cb00f` (`grep -rn "Fold("` outside `viper/ast/` is empty).
**`Exp.Unfolding` works and was inherited**, not built here. `Stmt.Unfold` has two live sites that cannot
fire.

Consequence, **as inference not proof**: a custom predicate can be **assumed and consumed but never
established**, so positive cases risk **passing vacuously**. **This is NOT unsoundness** — Viper
preconditions are assumptions by design. Keep that qualification attached.

**The `custom_predicates_unfold` golden sharpens this and is the most informative committed artefact on the
branch.** The custom predicate does not sit *alongside* the class permission predicate — it **contains**
it:

```
predicate ordered(v) { isSubtype(...) && acc(Interval_unique(v), write) && ... }
method readUnderPredicate(i) requires acc(ordered(i), write)     // and NOT Interval_unique(i)
{ v_ret_0 := havoc(intType()) }                                  // 'return i.hi' became HAVOC
```

**So requiring a custom predicate means holding no direct field permission, and a field read under it
silently havocs rather than erroring.** With `fold` unconstructible, a caller can never see the body.

**Root cause, one finding with several witnesses rather than several findings:** with no user-reachable
`fold` and the class permission predicate nested inside the custom predicate, a predicate's body is never
available to any caller, so **every attempt to write a negative case about predicate CONTENT collapses into
a permission or havoc observation.**

**Standing stop-and-escalate: if a control that omits the predicate still VERIFIES, stop and escalate
immediately.** It outranks everything. It has not fired — and note *why* for Method A: the A-2 adversarial
functions vary the predicate's **truth**, not its **presence**, so the trigger is structurally unable to
fire on those files. No predicate-omitting control exists in the A-2 set.

## 6. Negative cases — satisfied by failing FOR THE STATED REASON

**A negative case is not satisfied by failing. It is satisfied by failing for the stated reason. Require
the diagnostic text, never the exit status.** A permission-ground rejection where a predicate ground was
expected is a **false negative control that reads as success**. If the intended ground and the permission
ground cannot be told apart from the diagnostic, **that is a reportable finding, not a failed case.**

### N-2 set (12 cases: N1×3, N2×2, N3×4, N4×3)

Static, from committed source and committed goldens; none run-verified. Full detail belongs in the
companion analysis doc.

- **Sound, 6 of 12** — fail for the stated reason: `n1_nonboolean`, `n1_subexpr`, `n2_typeparam`,
  `n3_ifcond`, `n3_argument`, `n4_missing_precondition_at_callsite`. The last is the **only unambiguous
  N4**: its rejection is a permission message *about the predicate itself*, so the two grounds coincide
  rather than compete.
- **Two false negative controls — reportable findings, not failed cases.** `n4_weak_precondition` fails,
  but `lo >= 0` is never learnable, so the postcondition would fail identically if the predicate were
  strong enough; "postcondition might not hold" cannot distinguish "too weak" from "body invisible".
  `n4_mutated_object` — the highest-value and most misreadable case — either rejects on insufficient
  permission (permission ground where predicate-falsification was expected) or **verifies**, because
  mutating a field does not consume a predicate access. **The verifying outcome is NOT the escalation
  trigger** (that needs a control which *omits* the predicate; this one supplies it) and is **not
  unsoundness**. What it shows is that the property N4 exists to cover is unreachable while `fold` is
  unconstructible.
- **`n2_nullable` does not fire `PREDICATE_WITHOUT_CLASS`.** `embedTypeWithBuilder` carries nullability as
  a **flag** and recurses the pretype to the non-null type, so `Node?` yields `ClassTypeEmbedding(Node)`.
  Silent acceptance with nullability dropped. **If an update pass writes an empty golden here and it reads
  as a passing negative control, that is the trap firing.**
- **`n1_property` is vacuous.** `val x = predicate { true }` is a `FirProperty`; it fails the
  `as? FirSimpleFunction` cast and the `!is FirSimpleFunction` gate at `ProgramConverter:471`. Nothing is
  converted and nothing reported. **It is the only one of the 12 whose empty committed golden is correct,
  so the only one that would pass a non-update gate — while testing nothing.** Coverage gap, not
  unsoundness.
- **`n3_enclosing`** fires, but on a sibling method of the same class, i.e. behaviourally the
  already-covered "ordinary function body" case. Its diagnostic may be source-less, since `insertCall`
  reports on `(subject as? WithPosition)?.source` and an implicit dispatch receiver may not be a
  `WithPosition`. Expect its golden to look unlike its siblings; that is not a defect.
- **Open, medium confidence: `n3_lambda`.** Fires only if the lambda body is converted; going through a
  local `val` adds an untraced resolution step. The one N3 case that may silently produce nothing.

## 7. Method A set (5 cases) — vacuity is structural, but they are not worthless

- **A12, A3, A10, and A13's `useNonDecreasing` are empty-bodied `fun useX(p: T) { preconditions { p.pred() } }`.**
  Nothing is asserted, called, or consumed. They would all still verify with the predicate body replaced by
  `true`, so **on semantics these four are vacuous by construction.**
- **The qualification that belongs on the record:** what they *do* discriminate is **well-formedness of the
  emitted predicate body and its surrounding declarations.** That is not hypothetical — it is exactly the
  strategist's §0 finding 2 (recursive `next(...)` precondition unprovable inside the body), fixed by
  `includeSubTypeInvariants()` at `e1cd7c1c`. An empty-bodied case **did** fail before that fix. **Read
  these four as emission/well-formedness cases with a real discriminator, not as verification cases.**
- **A3 `name_collision` is the one case defensible as written.** Two unrelated classes both declare
  `ordered()`; the discriminator is content-independent and structural — if `ShortNameResolver` fails to
  qualify, two Viper `predicate ordered(...)` declarations collide and the program is rejected at
  consistency-check, not by Z3. `useBothOrdered` forcing both into one program is what makes it bite.
- **A10 `scoping`** claims no second predicate is minted for a `Derived` receiver. That is a claim about
  **generated text**, so its evidence lives in the `.viper.diag.txt` golden. **A pass alone does not
  establish it** — a distinct vacuity route from §5's.
- **A12 `composition`** is the case most likely to fail for an interesting reason: cross-class mutual
  recursion with a forward reference, where `includeSubTypeInvariants()` supplies the *subject's* invariant
  and may not supply the partner class's at the point `partner.bInv()` is named.
- **A11 `postcond_loopinv` is the exception and the important one.** `SignatureCreation.kt:159-202` puts
  postconditions into the signature as `ensures`, not an inhale. **So `makeOrdered` must ESTABLISH a
  predicate access on a freshly constructed object, and with no `fold` there is no visible means to do
  it.** Inference, not proof, not a soundness claim: both A11 positives are predicted to fail with an
  insufficient-permission-to-exhale error. **The nasty part, a new member of the false-reading family: if
  the positives fail, the adversarial counterparts fail TOO — for the no-fold reason, not for violating the
  invariant.** That is a false negative control that reads as the case succeeding. **A11 must be judged on
  diagnostic text, and the positive and adversarial error texts must be compared and shown to differ.** If
  `makeOrdered` *does* verify, that is the most informative outcome available in the whole set, since it
  would mean a fold-equivalent exists that §5 has not found.
- **Defect needing no run: `custom_predicates_a2_val_recursive.kt`'s `buildAndUse()`** returns
  `head.nonDecreasing()` from a **method body**. The repo's own `predicate_outside_specification.kt:12` is
  the same shape and expects `<!PREDICATE_OUTSIDE_SPECIFICATION!>i<!>.ordered()`. `ProgramConverter` gates
  on `inSpecification`/`specificationDepth` and emits the diagnostic; the line carries no marker.
  **`buildAndUse` cannot be a positive case at all**, since the only legal place to name a predicate is a
  specification block. Preferred fix is deletion rather than marking, since a marked copy duplicates an
  existing test.

## 8. The runner invariant and the merge gate

`PhasedDiagnosticTestGenerated.java` is **generated but tracked and not gitignored**, and upstream keeps it
in step with its inputs (111↔111 at `bf32366c`, 116↔116 at `e1cd7c1c`). The feature tip breaks it by 21.

- The runner is **not forbidden** on the branch. It must be **regenerated exactly once, LAST**, after the
  final test file lands. Not all files are on the feature branch yet, so any regeneration now would be
  superseded.
- Until then the branch sits inconsistent and **the build self-heals** via
  `compileTestKotlin dependsOn generateTests`. Acceptable temporarily.
- **Solvers: leave it dirty, say so, never commit a partial regeneration, never revert someone else's.**
  Both directions of churn cost a commit and neither is the fix. Two commits of churn already resulted
  (`1f38d14a` partial, `6d2e0518` full, `458cb00f` revert).
- **The final regeneration is a MERGE GATE owned by the aggregator, not a solver task.** A partial
  regeneration is visibly wrong and gets superseded; an **absent** one is invisible and merges.
- **Never coordinate a merge of the generated runner between solvers.** A generated file's conflicts cannot
  be resolved by reading them, because the content is not authored.
- Pre-commit check for whoever regenerates, needing no build: `runTest(` count must equal the on-disk `.kt`
  count. `1f38d14a` was detectable before committing by exactly two greps.

## 9. Host and lock discipline

~11 GB host. **Concurrency was the problem, never the 6g ceiling.**

```
flock -w 3600 -E 75 /tmp/snakt-gradle.lock bash -c 'echo LOCK-ACQUIRED-$(date -Is); ./gradlew :formver.compiler-plugin:test -Dorg.gradle.jvmargs="-Xmx6g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8"'
```

- `-E 75` makes a lock timeout exit exactly 75 and nothing else. This run's distrust of exit codes is
  gradle-specific and does not transfer to `flock`.
- `flock` gives **no fairness guarantee**, so queue position is not FIFO and a waiter can be overtaken
  indefinitely. **Never infer anything from how long a solver has waited.** (Observed once as FIFO in
  practice; that is not a guarantee.)
- **Re-runs are budgeted at TWO**, then the case is handed back as a non-result. **This does not go up.**
  Only a bound makes an unbounded wait *reportable*.
- **No `LOCK-ACQUIRED` line → never got the lock; re-running is correct and cheap.**
- **`LOCK-ACQUIRED` present with no `BUILD` line → non-result.** Two sub-classes, and they differ:
  - **Diagnosed crash** — `Gradle build daemon disappeared unexpectedly`. **Two sightings today, both on
    golden-update passes**, zero on plain runs. A retry under budget is legitimate, but treat it as
    probably reproducible: **if a second identical attempt dies the same way, do not spend the third.**
    `build/test-results/test/` is then **absent**, because the JVM died before JUnit flushed.
  - **Undiagnosed (the exit-144 class)** — do NOT silently re-run. Report it with its log and stop.
- **An absent or empty `build/test-results/test/` is the signature of a JVM death, not evidence of zero
  tests.** The opposite failure from the blended-count problem below.
- **The XML directory is never cleaned**, so a naive glob blends stale runs in both directions. Parse the
  boundary back out of the `LOCK-ACQUIRED` marker (`date -d "$MARK" +%s`) and aggregate only XML with
  `mtime >= BOUNDARY`. Report `files=` and `stale_skipped=`; a nonzero `stale_skipped` is itself a datum.
  Chosen over `clean` deliberately, since lock slots are the binding constraint.
- **Redirect a long chain to a file and grep the file.** `tail` buffers until the script exits and hides
  your own instrumentation.
- **Judge every gradle run by its captured log text; reported exit codes are unreliable here.**

## 10. Standing prohibitions and traps

- **Never `git stash`.** `refs/stash` is a **single ref shared by every worktree** of this repo. One
  solver's `stash pop` consumed and dropped another's entry and applied the wrong files; recovery was
  verified byte-identical, but it nearly cost four files. Use `git switch --detach <commit>` for baseline
  isolation, or `mv` into `/tmp`.
- **Never pattern-kill (`pkill -f`).** It is inconsistent across sessions, may succeed, and may SIGKILL
  sibling Claude sessions.
- **The golden-update trap is the sharpest one.** An update pass rewrites the **test source** as well as the
  goldens, inserting `<!VIPER_TEXT!>` / `<!VIPER_VERIFICATION_ERROR!>` inline markers, so program and
  expectation are both bent to observed behaviour. **No golden from an update run without a human-legible
  diff you have read.** Require a final non-update run green without having rewritten any golden.
- **Required for every update pass: read whether ANY NEGATIVE CONTROL WAS WRITTEN INTO A GOLDEN AS
  PASSING.** That single read catches the update trap and the vacuity problem at once.
- **`@Manual` is untested in either direction.** Do not re-derive the retracted claim from
  `acc_precondition.kt`, which is `// NEVER_VALIDATE` → `conversionOnly` → `FORCE_DISABLE` and never runs
  Viper.
- **Do not touch the deliberate error** in `custom_predicates.viper.diag.txt`.
- **`Builtins.kt` KDoc defect**, already reported: its doc comment for `predicate` demonstrates
  `next!!.sorted()` while `!!` cannot compile plugin-wide (`FirCheckNotNullCall` "Not yet implemented").
  Solvers were told not to fix it — six of them share that file.
- **`[UNVERIFIED]` in the commit subject is the run-wide convention** until that solver's own gate passes.
  A subject line stating its own verification status cannot be misread by someone later scanning `git log`.
- **cwd drift is the run's most prolific false-reading source** — five sightings, more false readings than
  any real defect. A relative-path grep from a drifted shell returns a silent zero. **Use absolute paths for
  every count.** Three apparent write-guard refusals were cwd drift.
- **Do not count processes with `ps | grep`** — the grep's own pattern matches itself and the count changes
  with how you asked, which looks like the system moving when it is the instrument moving. Use
  `pgrep -x flock`.
- **A count is only as good as the pattern that produced it.**

## 11. Measuring a session's real context size

`intersession peek` does **not** surface it. Read it from the instrument:

```
/opt/bots/log/cost-watcher/silverbot/<persona>/<session_id>-live.json    field: last_ctx_tokens
```

Check `updated_ms` for staleness. **Do not trust a session's own sense of its size** — one seat parked at
143k estimating 100k, and two solvers measured ~10k above what had been reported for them. **Never
arbitrate between two senses of a context size; read the instrument.** Warn 150k, crit 200k.

**Verify liveness from the process table, never the manifest.** One solver read `terminal exit reported`
and `AWAKE, working (mid-turn)` simultaneously all afternoon while alive; conversely `AWAKE, idle (between
turns)` usually means correctly parked on a backgrounded gate run, not wedged.

**Resolve queue state from `/proc/PID/cwd` on `pgrep -x flock`, and read `-Dformver.testMode=FULL` out of
the live test executor's `/proc/PID/cmdline`.** That is evidence about what **is running**, whereas a green
test count is evidence about what a command **reported**. Every false green in this run came from accepting
the second where the first was needed.

## 12. Write-guard behaviour, as measured from a dispatcher seat

The guard pattern-matches the **raw text of the whole command line**, including paths inside quoted
arguments, so quoting a forbidden path in a message body is refused identically to acting on it.

**Refined here:** a `Bash` command writing outside the seat's own working directory is refused **even for
the shared artifact root** (a `mkdir -p` into it was refused), while the `Edit`/`Write` tools against the
same tree succeeded. So Bash-mediated `cp` into a shared root works from a solver's own seat but not from
another's. **Read-only `git -C` against the main checkout is sanctioned.** If a refusal sighting arrives
without its cwd and exact command text, mark it unreliable rather than dropping it.

## 13. State of the work

| Solver | Slug | Commit | Where | Files |
|---|---|---|---|---|
| felix | a-1 | `b0bf797a` | `origin/solve/solver-a-1` | 6 |
| zara → successor | a-2 | `4be0c15a` | **feature branch** | 5 |
| barr | b-1 | `af5c2652` | `origin/solve/solver-b-1` | 3 |
| saskia | b-2 | `e10bcf18` | **feature branch** | 4 |
| soren | n-1 | `819ba770` | `origin/solve/solver-n-1` | 9 |
| briar → successor | n-2 | `4ead163e` | **feature branch** | 12 |

All `[UNVERIFIED]`. Feature tip `458cb00f` carries 21 files (5 a2 + 4 b2 + 12 n2); `4be0c15a`, `e10bcf18`
and `4ead163e` are all confirmed ancestors of it, so **nothing any solver committed is at risk.**

Aggregation is uneven by design and the dispatcher owns sequencing it. barr is deliberately holding its
feature-branch push because rebasing would rewrite a tree its queued runs execute against; that was
endorsed and preservation is achieved either way. **Never open a second PR (#30 only) and never base on
`pipeline/strategist-bundle`.**

## 14. Step 5 provenance

Five of six solvers were asked for the **verbatim** relay instruction in their role file. **All that
answered report that NO relay or handoff section exists.** A role file has five sections (header, Your
Task, Execution, Standing Rules, Run Context) and the only statement about output is:

> `**Output:** Committed attempts + report at the output path in Run Context + completion marker at the
> completion path in Run Context`

It names no agent to spawn, nobody to report to, no handoff. The only spawn-related sentence in the bundle
is in `agents/shared/standing-rules.md` under `## Respawn Protocol`, conditional on an agent the solver
itself spawned dying — **unreachable for a solver seat**, since a solver is given nobody to spawn. One
solver confirmed from inside its own seat that its bundle holds exactly two files. Four-for-four settles it.

## 15. Process lessons worth keeping

- **When you catch your own error, say so with the remediation attached, in the same turn.** The
  self-catches are the reason this run is recoverable.
- **When you retract, say which half failed and what survives.** Withdrawing a whole two-part claim because
  one part failed already cost this run a real finding for two turns.
- **Before passing a number onward, ask what you already hold that bears on it.** The retracted 136 was
  passed on by the seat that held its own refutation.
- **A correction that lands only where it was noticed is a defect class this run hit three times.** Put
  rulings in the durable doc, not only in messages.
- **Label every rule handed to a subordinate structural or hygiene, and resolve ambiguous cases as
  structural.** The errors are asymmetric: a hygiene rule mistaken for structural costs time; a structural
  rule mistaken for hygiene costs the result.
- **Write state incrementally as you go, not at handover.**
- **De-escalate your own alarms when the arithmetic says to, and say that you are doing it.**
