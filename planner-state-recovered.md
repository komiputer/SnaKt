# Planner run state — custom-predicates

Revision 5, by `remo` (Planner seat, replaced `auden`), amended continuously through its turn rather
than written at handover. Rev-5 additions are marked "rev 5" inline. Revision 4 was by `auden`.
Revision 3 was written by `chime` (Planner/relay seat, successor to `cato`) on reaching
~128k context; revision 4 is by `auden`, its spawn-replacement, and marks its own additions inline as
"rev 4". Everything is verified in the writing seat unless marked otherwise. Where this contradicts an
earlier revision, **this file wins** — revisions 1, 2 and 3 all carried defects, listed under
"Defects in earlier revisions".

## Read these first, in order

1. `complete/custom-predicates-step-1.md` — feature brief. **Its gate instruction is wrong**; see
   "The gate". Everything else stands.
2. `intake/custom-predicates-clarifications.md` — operator design decisions (Q1–Q6).
3. `complete/custom-predicates-step-2.md` — what shipped, including 2c.
4. `testing/custom-predicates-strategy.md` (§5 = post-2c reconciliation) and
   `testing/custom-predicates-plan.md`.
5. `complete/custom-predicates-step-3.md`.
6. `surface/custom-predicates-api.md` — **re-check before citing.** Revision 1 recorded its "§4" as
   false for claiming recursive predicates verify. 2c made recursive predicates verify, so that claim
   may now be true. Unconfirmed: the file has no `## 4` heading, so the reference was to a numbered
   item elsewhere.

## Live instances — names matter

Rev 2 omitted the 2c worker's name and the seat that read it nearly concluded a live worker had been
orphaned. **Name every live worker.** Check liveness via `intersession peek <name>`; workers are
re-rooted onto a replacement seat, so `listener=instance:silverbot/<current-seat>` is expected.

| Name | Role | State at rev 3 |
|---|---|---|
| `niamh` | Launcher | active; owns artifact root, `artifacts-history`, PR #30 |
| `chime` | Planner/relay (this seat) | ~128k, parking after this file |
| `overse` | Step 2 Implementer | asleep, resumable |
| `cleo` | Step 2b Implementer | idle |
| `cato` | Step 1 Planner | retired (terminal exit reported) |
| `tobias` | Step 2c Implementer | idle, work landed |
| `vivian` | Step 3 Strategist | idle, parked, ~123k — low headroom, prefer a fresh strategist |
| `ennio` | Step 4 Solver Dispatcher | rev 4: awake, parked idle at 103k in `SnaKt-wip8`, awaiting its six solvers |
| `auden` | Planner/relay (rev 4 seat, replaced `chime`) | active, `SnaKt-wip15` |
| `otto` | Method B ruling Strategist (fresh, opus) | rev 4: spawned by `auden`, working |
| `barr` | Solver B-1, held by `ennio` | Method B reshape + negative controls, iter-1 |
| `soren` | Solver N-1, held by `ennio` | N4/N5; exited and auto-resumed once (see fault 6) |
| `zara` | Solver A-1, held by `ennio` | A11 pos+adversarial, A12, A10, A3, A13; was at 84k |
| `felix` | Solver, held by `ennio` | given the predicate-swap control |

**All six solver names are now known** (rev 4, from `ennio`): `barr` (solver-b-1), `saskia` (solver-b-2,
sonnet), `soren` (solver-n-1), `briar` (solver-n-2, sonnet), `zara` (solver-a-1), `felix`. `saskia` and
`briar` had not reported at handover.

**`ennio` was replaced at rev 4, at 144k of the 150k threshold, by `auden`'s deliberate call rather than
at the wall.** It wrote
`testing/custom-predicates-step-4-dispatcher-state-iter-1.md` (362 lines, md5
`d3b68627a20c08633da9640121d3e0ec`, **read back and verified before the spawn**, per the Launcher's
sequencing condition — `spawn-replacement` re-roots against what is *written*, not what the subject
knows). Its six solvers re-rooted onto the replacement automatically. Section 0 of that file is the
FULL-mode-command-and-captured-output rule, front-loaded because a dispatcher inheriting six solvers
mid-flight is exactly the seat where a stale green gets adopted.

**`ennio` disclosed a bundle-opening breach** in section 8 of its state file, with an explicit "do not
cite this dispatch as licence". Honour that framing: the risk was never the breach but a later agent
treating it as precedent. **A dispatcher does not open its subordinates' bundles.** Self-disclosing it
was the right call.

**Correction passed to the replacement:** the state file's section 9 says Step 5 is blocked on
provenance. **Stale — the operator has ruled proceed.** Step 5 dispatch is the Planner's call in any
case, so it is not the dispatcher's to make either way.

Re-rooting confirmed in the `auden` seat: `intersession workers` does **not** list `ennio`, because its
spawner field still reads `chime` while its *listener* is `auden`. Turn-ends route by listener, so
`ennio` reports here correctly. Do not read its absence from `workers` as an orphan.

**The six solvers' names are not known to this seat** — `ennio` dispatched them and holds them. Ask
`ennio`, don't enumerate the registry and guess.

Worktrees in use: `SnaKt-wip4` vivian, `SnaKt-wip5` tobias, `SnaKt-wip8` ennio. **Never name another
instance's worktree in a Bash command** (message bodies included); it is refused outright.

## Steps closed

| Step | Agent | Outcome | Commits |
|---|---|---|---|
| 1 Planner | cato | Brief, oracle search, clarifications | branch off `bf32366c` |
| 2 Implementer | overse | Pieces 1–4, piece 5 pure half | to `758633d6` |
| 2b Implementer | cleo | Piece-5 reduction, 3rd diagnostic, negative tests | `990e97b3` |
| 2c Implementer | tobias | **Recursive predicates verify** | `e1cd7c1c` |
| 3 Strategist | vivian | Methods A/B/N; §5 post-2c reconciliation | docs only |
| 4 Solvers | ennio | six solvers dispatched, iteration in flight | `solve/custom-predicates-iter1` |

- Feature branch `origin/feature/custom-predicates`, tip `e1cd7c1c`. **PR komiputer/SnaKt#30**,
  8 commits `5b949b27..e1cd7c1c`. Commits append. **Never open a second PR.** Its body discloses the
  verification defect and keeps the retraction visible.
- `origin` is the fork `komiputer/SnaKt`; `upstream` is `JetBrains/SnaKt`. Always pass `--repo` and
  `--base` to `gh pr create` — it defaults the base to the parent on a fork.
- Step 4's completion marker is `complete/custom-predicates-step-4-iter-1.md`, **unwritten by design**
  until the solvers report. Monitor for that file. Respawn Protocol salvage path:
  `salvage/custom-predicates-step-4-iter-1.md`.

## The gate — verified, not inherited

**A new feature's gate is FULL `:test`.** `:untilConversion` sets `CHECK_CONVERSION` and
`shouldSkipByTestMode` returns `true` for it (`VerificationFacade.kt:26`) — Z3 never runs, so green
means only that generated Viper *text* was stable. That misreading let a non-verifying feature clear
three consecutive gates. It is the run's central error.

Verified driver facts:
- `tasks.test` hard-sets `formver.testMode=FULL` (`formver.compiler-plugin/build.gradle.kts:105`).
- `getTestMode()` maps `null -> FULL` (`PluginPrototypeTests.kt:37`). A plain `:test` is FULL.
- `VerificationDiagnosticsCollector` owns `.viper.diag.txt` (`PluginPrototypeTests.kt:47`).
- Inline `<!VIPER_VERIFICATION_ERROR!>` markers are checked by `allTagCollector.assertEqual()` under
  FULL **only**. Step 2's note that markers "are not required" was an artifact of the wrong gate.

**A gate claim needs positive evidence that Silicon ran**, not merely absent failures. Cheapest strong
evidence: a non-empty `.viper.diag.txt` golden passing, which is impossible if verification was
skipped.

**The strongest available form, and the new standard — rev 4, `sacharissa`.** Check the **process table**
for `-Dformver.testMode=FULL` in the test executor's arguments while the run is live. That is **positive
evidence that FULL mode is running**, not absence-of-failure evidence. Every gate claim in this run before
`sacharissa`'s check on `soren` rested on the weaker form. Prefer this whenever a run is still in flight.

### Three further ways a green run means nothing — both found by `barr` via `ennio`, rev 4

The conversion-only gate is not the only false-green route. Three distinct classes now exist, and a
gate claim must rule out all three.

1. **The golden-update trap.** A new case has no goldens, so an update pass is unavoidable — and update
   mode will happily write a golden that *encodes* `VIPER_VERIFICATION_ERROR` and then report the build
   green. A green run taken after an update pass is evidence of nothing at all. Required discipline:
   read the generated goldens and inline markers yourself, then require a **final non-update gate run
   that is green without having rewritten any golden**. This is the sharpest trap in the run, because
   it produces a green that survives the FULL-`:test` correction. **Launcher ruling, rev 4: no golden is
   ever accepted from an update run without a human-legible diff.** Whoever synthesises must read this
   before running any update pass.
   - **The mechanism, rev 4, `soren` reading `TagCollector.kt`:** `assertFileEqual` compares against
     `originalTestDataFiles.single()` — the `.kt` source itself, rendered with tags. So an update pass
     does **not** merely write a `.viper.diag.txt`; it **rewrites the test source**, inserting inline
     `<!VIPER_TEXT!>` / `<!VIPER_VERIFICATION_ERROR!>` markers. **The program and its expectations are
     both rewritten to match observed behaviour.** That is *why* a post-update green is worthless, and it
     explains the class rather than just naming it.
2. **Exit 144 with no `BUILD` line at all — UNEXPLAINED, rev 4, `soren` via `ennio`.** Two gradle runs
   died with ~2-line logs ending `Reusing configuration cache`, and **no `BUILD SUCCESSFUL` or
   `BUILD FAILED` either way**. 144 = 128+16 (SIGUSR1). No OOM in the kernel log, and memory has since
   recovered to ~4 GB under `flock`, so memory does not account for it. Both runs correctly discarded as
   non-results. **This is the nastiest of the four classes because it emits no misleading signal — it
   emits none.** Treat a run with no `BUILD` line as no run, never as a pass. Cause unknown; do not
   spend a solver's context hunting it.
   - **CORRECTION, rev 4: "no `BUILD` line → non-result → re-run" conflates two causes with opposite
     correct responses, and as written it livelocks.** Found by `sacharissa`. A **`flock` timeout** also
     exits non-zero with no `BUILD` line, and is indistinguishable from the exit-144 class *by log text
     alone* — but the prescribed response, re-run, re-queues at the back of a saturated lock. Six serial
     runs at ~6–7 min each is ~40 min of tail against `-w 3600`, and new cases invoking Silicon may be
     slower than the 6m17s baseline.
   - **Also use `flock -E 75`** (Launcher, rev 4), *together with* the marker below rather than instead of
     it. `-E` sets the exit code used when the lock cannot be acquired, so 75 and nothing else means
     timeout, distinguishable from every gradle code. The reason this run distrusts exit codes is a
     **gradle-specific** fault and it does not transfer to `flock`. The exit code reaches the wrapper
     immediately; the marker survives in a captured log a later reader inherits. They fail differently,
     which is the point of having both.
   - **Budget re-runs: two attempts, then hand the case back as a non-result and stop.** The discriminator
     removes the livelock but not the thing underneath it — with six waiters and ~40 min of tail, an
     unbudgeted "non-result → re-run" can consume the whole window even when every re-queue is correctly
     diagnosed. **A bounded failure is reportable; an open-ended wait is not.**
   - **The discriminator, textual rather than exit-code-based** (the run does not trust exit codes). Wrap
     as `flock -w 3600 /tmp/snakt-gradle.lock bash -c 'echo LOCK-ACQUIRED-$(date -Is); ./gradlew …'`.
     Then: **no `LOCK-ACQUIRED` line → the lock was never obtained**, and re-running is correct and cheap.
     **`LOCK-ACQUIRED` present but no `BUILD` line → the real unexplained class**, which must be reported
     with its log rather than silently re-run. A `flock` timeout emits no gradle output at all; the
     exit-144 sightings had ~2 lines ending `Reusing configuration cache`, so they had already entered
     gradle.
3. **The contaminated baseline.** `generateTests` regenerates the runner against whatever is on disk
   when it reaches that task. A baseline run that overlaps a worker writing test files picks them up
   mid-flight — `barr`'s did, leaving `PhasedDiagnosticTestGenerated.java` modified. Reported as
   136/0 it would have put a false green under four brand-new cases. **A baseline must be taken before
   writing any test file, or retaken after.**

Run with `-Xmx6g`: `./gradlew test -Dorg.gradle.jvmargs=-Xmx6g`. `gradle.properties` pins `-Xmx2g`,
which is **not enough** once every test invokes Silicon — it kills the daemon. **CI sizing item.**

**Always quote a test count with the command that produced it**, or it misleads exactly as an exit code
without its log does. At `e1cd7c1c`:
- `./gradlew test` (whole project): **159 / 0 / 0**. 159 = 136 `formver.compiler-plugin` + 23
  `locality`. Verified in this seat; both tasks appear in the log.
- `./gradlew :formver.compiler-plugin:test` (the module task the plan names as the gate): **136 / 0 /
  0**, `BUILD SUCCESSFUL in 6m 17s`. Verified by ennio.

Both green, no verification gap. Rev 2 recorded "159" with no command attached and it briefly read as a
discrepancy; the Launcher had also quoted it onward uncommanded.

`./gradlew build` fails pre-existing on `generateTests`/`detekt` wiring. **Narrower than rev 1 said:**
`generateTests` runs clean in the `test` path, so the breakage is in the `build` path only.

## HEADLINE FINDING (rev 4) — custom predicates can be assumed and consumed, never established

Found by `ennio`, generalising `otto`'s `Fold` audit past where the Planner's ruling took it. **Every
link verified independently in the `auden` seat as well as `ennio`'s.** This is a **feature-level**
finding, not a test-methodology one, and it is the most consequential thing the run has produced.

Evidence chain:
- **`Stmt.Fold` is constructed nowhere outside `viper/ast/`.** A `grep` for it returns *nothing*. No
  program ever folds a predicate.
- **`Stmt.Unfold` at exactly two sites** — `Linearizer.kt:147`, `LinearizationVisitor.kt:435` — both
  behind `unfoldToAccess` guards, true only for the policy `Linearizer` has already diverted to `havoc`.
- **Predicate accesses enter a program only by `Stmt.Inhale`**: `StmtModifier.kt:32` for permissions, and
  `LinearizationVisitor.kt:256`/`279` inhaling type invariants — which is what `e1cd7c1c`'s
  `includeSubTypeInvariants()` feeds.

So the only way any program obtains `acc(P(x))` is by **assuming** it at a specification boundary, and
the only thing it can do with it is **exhale** it at a call site. **An obligation a call site creates can
never be discharged constructively, only passed along from another assumption.**

Consequences — stated as inference, flagged as such, and the reason iter-1's controls matter:
- **The feature cannot express that a constructor or factory produces an object satisfying its
  predicate.** There is no program shape that establishes a predicate for a concrete object.
- This is **not unsoundness** — preconditions are legitimately assumptions in Viper. But **a custom
  predicate constrains nothing about any object's actual state, so every positive case built on one risks
  passing vacuously.**
- It explains why `barr`'s body-content hole is **unclosable rather than merely unclosed**: with no
  `fold`, no program can ever be required to show a predicate's body matches reality.
- **It reframes A11/A12**: a verifying A11 postcondition may demonstrate only that *an assumption
  propagates*.
- **It bears directly on `surface/custom-predicates-api.md`'s claim about what custom predicates
  deliver.** Rev 3 already flagged that file as re-check-before-citing; this is now the reason to.

**Do not conflate the three constructs — rev 4.** "Fold/unfold" names three separate things here, and the
distinction is what makes the finding precise rather than sweeping:

| Construct | Sites | Status |
|---|---|---|
| `Exp.Unfolding` (pure expression, spec contexts) | `PureExpLinearizer.kt:91`, `SsaConverter.kt:117` | **Works**, auto-inserted, no user annotation needed |
| `Stmt.Unfold` (statement, method bodies) | `Linearizer.kt:147`, `LinearizationVisitor.kt:435` | Constructed but behind `unfoldToAccess`, which is true only for the policy already diverted to `havoc` — effectively dead |
| `Stmt.Fold` | **none outside `viper/ast/`** | **No constructor exists anywhere in the plugin** |

So "the `unfold` half came free from `PureExpLinearizer`" refers specifically to **`Exp.Unfolding`**, the
pure-expression form. That is a genuine working capability and it is why recursive predicate *bodies* are
well-formed. It is **not** `Stmt.Unfold`, and it does nothing for method bodies.

Prefer **"two live sites that cannot fire"** over "dead" for `Stmt.Unfold` (the Launcher's wording, and
better than this seat's): both sites sit behind `unfoldToAccess`, true only for
`BY_RECEIVER_UNIQUENESS`, which `Linearizer.kt:114` has already diverted to `havoc`. That tells a future
reader the wiring exists and is one predicate away from working — which `Stmt.Fold` never will be.

**This is a property of the plugin, not a gap our change left — verified on `main`, rev 4.** Checked in
two seats against the **main checkout**, at `bf32366c`, which is the exact commit
`feature/custom-predicates` was cut from and carries none of our work:
- `Stmt.Fold`: **zero hits** outside `viper/ast/` on `main`. **The absence is pre-existing upstream.**
- Both `Exp.Unfolding` sites (`PureExpLinearizer.kt:91`, `SsaConverter.kt:117`) **already exist on
  `main`**. So the working half was not merely "free" — it was **inherited, not built by us**.

This is the framing the operator needs, and it is the difference between **something we owe and something
nobody could have delivered under this instruction**. "Implement fold/unfold automation" asked for a half
that has no constructor anywhere in the plugin we built on.

**Rev-3 open item, now settled.** Rev 3 flagged that `surface/custom-predicates-api.md`'s "§4" was
recorded false by rev 1 but the file has no `## 4` heading. It refers to **numbered item 4 of "Compiler /
plugin behaviour visible to callers"**, the claim that `unfolding` is inserted automatically in
specification contexts. **Post-2c that claim is TRUE**, confirmed by the two `Exp.Unfolding` sites above.
Rev 1's "false" reading no longer applies. Stop re-checking this.

Cheapest available test, dispatched by `ennio` to `felix` and `zara`: negative controls plus a
**same-arity predicate swap**, with a standing instruction to **stop and escalate if a control that omits
the predicate still verifies**.

## Feature state after 2c

Fix is one line: `includeSubTypeInvariants()` in `ClassTypeEmbedding.customPredicate`. A predicate
access does not expose its body, so a `val` read embedding as a Viper function requiring
`isSubtype(typeOf(subject), C())` had no justification for that precondition. Reached only for
user-declared predicates; output preservation held, verified rather than assumed.

- `useSorted` and `useDescending` verify with no error markers. **Recursive predicates work.**
- `Node.value` had to become `val`. **A13: every property a recursive predicate touches must be
  `val`**, not only the link — a `var` read in a predicate body has no justified permission. This
  constrains the predicate *body*, so it is narrower than the method-body non-coverage below. Given to
  solvers as a prohibition to confirm, not to challenge.
- 2c also closed **A4** (dispatch-receiver subject, via `Chain.descending()`), an
  implemented-but-never-executed path. **A6 and A4 are regression-only; add nothing for them.**
- Remaining Method A gaps, priority order: **A11** (`postconditions`/`loopInvariants`, entirely
  untested, top gap), A12 (multiple/mutually recursive), A10 (class scoping), A3 (name collision).
- Methods B and N unchanged. **N4** (genuine verifier rejection) gained value now the suite is green:
  it is the only class that can catch a regression in what Viper actually rejects.
- Strategy §0 is kept, marked superseded. Its lesson still binds: a conversion-only gate reading as
  verification evidence is a property of the two-tier driver, not of that one bug, so every case must
  name its gate.

## Accepted non-coverage — do not reopen

Piece 5's **impure half**: reading a field through a predicate inside a method body. Verified three
times independently. In `Linearizer.addFieldAccessStoringIn` the `AccessPolicy.BY_RECEIVER_UNIQUENESS`
branch emits `havoc` and shadows the `unfoldToAccess` unfold path below it, which `FieldEmbedding.kt:71`
defines as that same policy — dead code under a standing TODO at `Linearizer.kt:115`. A `var` read is
havoc'd before permissions are consulted; a `val` is not a field. Recorded truthfully as `useOrdered`'s
failure in `custom_predicates.viper.diag.txt` with an inline marker, rather than deleted.

`!!` is unimplemented plugin-wide (zero `CheckNotNull` handling). Recursive examples need a `val` link.
Out of scope, documented as a limitation.

## Step 5 — UNBLOCKED by operator ruling, dispatch with provenance recorded

`synthesizer.zip` is **placed** at `handoffs/synthesizer.zip`, md5
`d843046ea69f9106e4585186ce657684`, **1104 bytes**, unopened. **The operator has ruled: proceed with it,
cross-generation and all.** The Launcher flagged the mismatch risk explicitly and the operator said
go — so this is a **documented operator decision, not a substitution anyone inferred**. Record it that
way in the step doc.

**When briefing the Synthesizer:** expect its Run Context fields to possibly differ from what ennio's
role file passes through. If they do not line up, that is **the mismatch surfacing, not a worker
error** — report it, do not paper over it by inventing field values. An artifact bent to fit a role
file is how a run looks complete without being complete.

Provenance, which whoever dispatches it must know:
- It is **not** in our own delivery. The Launcher enumerated `orchestrator_3.zip` exhaustively: the
  outer archive has 4 files and no bundle in the chain contains a synthesizer. That finding stands.
- It comes from `/home/silverbot/data/received/orchestrator-x/`, dated Jul 27, **three days older**.
  Its `solvers.zip` is 12518 bytes against our 21911.
- **Verified in the `auden` seat (rev 4), by checksum against the source:**
  `orchestrator-x/agents/synthesizer.zip` is md5 `d843046ea69f9106e4585186ce657684`, **1104 bytes** —
  byte-exact to the placed file. So the placed bundle's provenance is confirmed from the source side,
  not just asserted.
- **The 1104-vs-1978 discrepancy: both numbers are correct and neither was a copying error.** 1104 is
  the **compressed file size**; 1978 is the **uncompressed size of the single entry inside**, taken from
  `file(1)` output by the Launcher and recorded by rev 3 as though it were the file size. So this was a
  **units mislabel, not a transcription slip** — `auden` first recorded it as chime's error and the
  Launcher corrected it. **Quote which size you mean.** This is the same failure class as quoting a test
  count without its command, which the run has now hit three times: a number carried onward without the
  measurement that produced it.
- `unzip -t` on the placed file: it contains **exactly one entry, `instructions.md`**, and no nested
  bundle. Consistent with a terminal step. (Integrity check only, not an open.)
- **"Different role set" overstates the mismatch.** Directory listing of `orchestrator-x/agents/` gives:
  comparator, debugger, implementer, meta-reviewer, planner, reviewer, solvers, strategist,
  synthesizer. That is a **superset** of our spine — planner → implementer → strategist → solvers is
  present in the same order, with the synthesizer sitting after solvers — plus four extra roles we do
  not have. The mismatch risk is therefore materially **lower** than rev 3 feared, though still real
  and still unverified from inside the bundle. Do not treat this as licence to invent field values.
- So its Run Context fields may not match what ennio's role file passes. **The operator has been asked
  to confirm before anyone dispatches it.** Until then the Step 4 → Step 5 relay stays blocked, on
  provenance rather than absence.

**Why the artifact was absent — now settled from a third direction.** Solver `barr` (solver-B-1)
quoted its role file verbatim: it has **no relay or handoff section at all**. Its only output statement
is line 22, "Committed attempts + report at the output path in Run Context + completion marker at the
completion path in Run Context", plus "Work independently". It names no agent to spawn, report to, or
hand off to. So the solver role files were never written to relay onward, and `synthesizer.zip` is
absent from our set **by construction rather than mislaid**. The verbatim-quote request earned its keep;
keep collecting the remaining five.

Earlier, verified-by-listing finding, still true and worth keeping: the carry chain neither stops at
Step 4 nor fans out. `solvers.zip` has 10 entries and no synthesizer; all eight
`solver-{a,b,n,v}-{1,2}.zip` contain exactly `instructions.md` + `agents/shared/standing-rules.md`. So
nothing in *our* delivered set supplies it.

`auditor.zip` remains **unused and unexplained** — best reading, consistent but unconfirmed. The
pipeline is mode-branching (`Run mode: {new-feature | polish}`) and the Auditor is the polish-mode Step
2 spawn; three independent things point that way and nothing against, but confirming requires opening
it, which Opaque Subordinate Bundles forbids. **Do not dispatch it, do not hunt for its step, do not
spend context on it.** With the operator. It and the synthesizer question may share one answer: a
bundle set narrower than the role files assume.

**`dist-path` was never supplied, deliberately.** Launcher ruling: there is no single dist-path, because
the worktree allocator assigns feature-repo locations (`--repo <base>` gives each worker a fresh tree).
Where `dist-path` means "the feature repository" it is the worker's **own** allocated worktree, never a
peer's. Where it means "where to find the next bundle" it is `handoffs/`.

## Pipeline shape — corrected

Roles nest, each bundle carrying its own subordinate:
`planner.zip` → `implementer.zip` → `strategist.zip` → `solvers.zip`, and there it stops.

- **Step 4 is the Solver Dispatcher, from `solvers.zip`.** Authoritative: vivian's role file, *"After
  writing `complete/<feature-id>-step-3.md`, spawn the Solver Dispatcher from `agents/solvers.zip`."*
- **Step 5 is the Synthesizer.** It exists as a step; only its bundle was missing (see above).
- **Verbatim role-file quotes are the only thing that has settled a question on this chain.**
  Structural inference about it has been wrong three times: the Launcher's nesting argument, and this
  seat's "nested in solvers.zip" and "fans out into the solver bundles". Prefer a quote to a guess.
  The six solvers were briefed to quote their own relay instruction verbatim — collect those, they
  reveal what Step 5 was meant to be independently of the artifact.

## Bundle delivery — the mechanism that actually works

**A bundle never travels through a spawn.** `intersession spawn-worker` has no `--bundle` flag
(verified) — only `--briefing FILE` and `--message TEXT`, both of which inline text. Bundles travel by
being **placed in `handoffs/` unopened**, with the path **named in the briefing**, and the **Run Context
sent as a separate message**. That is how every bundle in this run reached its consumer.

This resolves the apparent conflict between Opaque Subordinate Bundles and role files that direct a
dispatcher to "fill in each subordinate's Run Context": the fields that live inside a subordinate's zip
are read by *that subordinate*, not by the dispatcher. **Ruled in this seat: a dispatcher does not open
its subordinates' bundles.** A subordinate opening its *own* bundle is not evidence to the contrary —
those are the two sides the rule distinguishes.

The one field that does not survive is **model**, since the spawner sets it and cannot read an unopened
zip. Resolve by choosing it yourself per work shape (the standing expectation anyway) and recording the
choice. ennio's assignments: slot 1 **opus**, slot 2 **sonnet** — A slot 1 opus for adversarial A11/A12
design, N slot 1 opus for N4 rejection judgement — taken from its own dispatch table, not an in-zip
field.

Placed and verified: `planner.zip` `0163013671cfe219680387300735a444`, `implementer.zip`
`e816f349b2ec815cb332483a8b6d34b7`, `strategist.zip` `33ae9d4ac5bd44570eb14038cb004494`, `auditor.zip`
`d772c47b6126fa54328c1ae450904aeb`, `solvers.zip` `5a0cab79d517ea2b8259f7f8cfd360a5`,
`synthesizer.zip` `d843046ea69f9106e4585186ce657684`, and the six dispatched solver bundles:
`solver-a-1` `06460ccc1340d139f406361ac59c5ab5`, `solver-a-2` `39a5a8782eccc8c07cb14faacf55d371`,
`solver-b-1` `8b7af681cfa0655f571a15d60ed3b5e9`, `solver-b-2` `794108bcd8f661dc31bfdfd106b76877`,
`solver-n-1` `18cb908041da35126c3042c9c1bab692`, `solver-n-2` `f28af0b33d3e11499bb16e2696160f59`.

Integrity-check a bundle with **`unzip -t`** (validates without extracting) — that respects opacity.
`unzip -l` to establish a nested bundle's absence is acceptable; reporting what a subordinate's bundle
contains inside is not.

Unpack your own bundle into **your own tree**, never the artifact root: a role brief in the shared drop
is how an agent reads instructions not addressed to it, and duplicate bundles leave no authoritative
copy. (This seat did it wrong once; the Launcher caught it.)

## Artifacts vs code — two rules that do not cross

Feature-repo **code** is preserved by pushing to the feature branch (Standing Rules, Branch Writes).
**Artifacts** are preserved by the Launcher's `artifacts-history` snapshot. **Never `git init` the
artifact root** — it trips a cross-checkout guard and breaks the run for everyone. Both this seat and
the Launcher generalised Branch Writes to artifacts; the Launcher's version cost three stalls, this
seat's cost one message because vivian refused it.

If you cannot place a binary, hand it to the Launcher with a checksum, or push it as a git blob on a
**non-branch** ref. vivian used `refs/pipeline/solvers-bundle` (orphan commit `3db37f44`); a non-branch
ref cannot be listed as a branch or picked as a PR base, which matters because
`pipeline/strategist-bundle` at `8bc999d6` — scaffolding based on the older `758633d6`, which would
**revert** Step 2b — nearly reached PR #30. Delete the solvers ref once Step 5 holds its bundle:
`git push origin :refs/pipeline/solvers-bundle`.

## Faults escalated — do not re-investigate

1. **Reported exit codes are unreliable.** Four-plus sightings, including exit 0 with `BUILD FAILED` in
   the log, and once the reverse. **Judge every gradle run by its captured log text.** This is how the
   verification gap survived three steps.
2. **Duplicated inbox delivery.** One message arrived twice, byte-identical, as two separate events in
   consecutive turns. Correct handling: **act once, do not act again, flag it.** Harmless here; on a
   dispatch or a push it would not be. With the operator, paired with (1) as the same class — the
   delivery layer reporting something other than what happened. **A second sighting raises the
   threshold to paging.**
3. **THE WRITE GUARD IS RESOLVED — rev 4, by the Launcher, accidentally rather than by probing. The
   mechanism below supersedes both competing hypotheses recorded under this fault; the older text is kept
   only to show what was refuted.**

   **The guard pattern-matches the raw text of the whole command line, including path strings appearing
   inside quoted arguments it has no reason to treat as commands.** The Launcher discovered this because
   composing a *message* failed twice with a guard refusal naming the main checkout — the message
   contained no command touching it, only prose *describing* what had been done. A bare traversal command
   succeeded; the `send` whose payload quoted the path was refused.

   This retires both prior hypotheses and explains why each half-fit:
   - `chime`'s **syntax theory was directionally right and wrongly stated.** The guard *does* look at
     syntax — but at raw command text, not at path *form*. That is why `ennio`'s six absolute-path `cp`
     calls refuted it as stated while the underlying idea survived.
   - The **operation-based hypothesis** (`cp`/`unzip`/`Write` permitted, redirection and `rm` refused) is
     **not the rule.** It was an artefact of which operations happen to carry paths as **bare tokens**
     versus **inside quoted strings**.

   **The guard has a false-positive class, and it is worse than a merely conservative guard: describing a
   forbidden action in text is refused identically to performing it.** An agent reporting accurately on a
   boundary it respected gets blocked. That is a **direct incentive against the reporting discipline this
   whole run depends on**, so it is recorded as a fault rather than a quirk. *Practical consequence: if a
   `send` is refused, check whether your prose quotes a path before assuming you did something wrong.*

   **The refusal text itself gives a cleaner rule than "reading the main checkout is the one exception":**
   read-only `git` with the `-C` flag against the main checkout is **sanctioned**, and a subcommand with
   any writing form is refused whichever way it is meant (the refusal names two examples carrying `-v`
   flags). **Solvers wanting upstream history should be given this**, or they will reach for a directory
   change instead.

   **Downgrade earlier sightings accordingly.** Three apparent guard decisions in this run turned out to
   be **cwd drift** — the Launcher's `cp`, `ennio`'s, and one more. **Any refusal sighting recorded
   without its cwd and exact command text should be marked unreliable rather than dropped. The captured
   command text is the load-bearing datum, not the outcome.**

### Superseded — kept to show what was refuted

3a. **The write guard: syntax theory REFUTED; an operation-based hypothesis survives, untested.**
   - Refuted: "the guard matches literal path text, absolute refused / relative-after-`cd` allowed."
     ennio wrote **absolute** paths with no `cd`, cwd never leaving its own tree, and `cp` succeeded six
     times. An earlier revision claimed the syntax theory explained every observation; that was an
     overstatement by this seat, retracted.
   - A guard does exist: this seat met **explicit harness refusals** ("Refusing: this writes to …
     outside your working directory"), not command stderr.
   - Surviving hypothesis, **offered with its evidence and explicitly untested**: the split is by
     **operation**, not path form. Refused: `rm -rf <abs>`, `git cat-file blob … > <abs>` (shell
     redirect). Allowed: ennio's `cp <abs>` ×6, cato's `cp`, this seat's `unzip`. Fits every
     observation, including cato's, which the syntax theory fit only by accident.
   - **"Binaries cannot be written into the artifact root from a worker tree" is FALSE.** Try the direct
     write first. Rev 1's assume-refusal advice was inherited, and this seat passed it on too strongly.
   - **Two refusal-shaped failures were not refusals at all** — both cwd bugs whose stderr names the
     *source* or the *destination directory*: ennio's `cp: cannot stat <relative source>`, and the
     Launcher's `cp: cannot create regular file …` after its shell drifted into `handoffs/`. Anyone
     correlating stderr with guard behaviour would log these as guard rejections. **The instruction to
     the operator is to re-read the original sightings' stderr, not to re-test anything.**
   - **Rev 4: the Write-tool half is now answered, incidentally rather than by probing.** `ennio` wrote
     its 21588-byte state file as a **text file into the artifacts root from its own tree, and it
     succeeded with no refusal**. Combined with its six `cp` successes, the surviving shape of the
     operation-based hypothesis is: **`cp` / `unzip` / `Write` permitted; shell redirection and `rm`
     refused.** `ennio` held the line correctly — this was a write it had to make anyway, so it is an
     **observation, not an experiment**. Still do not probe deliberately.
   - **Do not use a relative-path workaround.** The guard states a real boundary (the allocator owns
     `~/dev` layout) and an evadable check is not permission. This seat declined to probe it even to
     vindicate its own theory; hold that line.
4. GitHub Issues oracle in `tools/search.py` is permanently dead — `JetBrains/kotlin` has
   `has_issues: false`. Do not retry.
5. An agent cannot name another instance's worktree path in a Bash command, message bodies included.
   Reading another instance's tree is refused outright.
6. **Manifest liveness fields can contradict each other — rev 4, found by `ennio`.** Solver `soren`'s
   manifest read `terminal exit reported` and `AWAKE, working (mid-turn)` **simultaneously**. It had
   exited and been auto-resumed. `ennio` checked the **process table** rather than trusting either
   field, found a live opus process at 31% CPU, and correctly did **not** respawn — respawning a live
   agent duplicates its work and re-roots its dependents. **Resolve any liveness question from the
   process table, not the manifest.** A less careful dispatcher would have read this as death and put a
   duplicate opus solver on the highest-value negative class.
   - Corollary checked in the `auden` seat and **negative**: `cato` is also marked `terminal exit
     reported`, so it might have been alive too. It is not — `ASLEEP`, window closed, **no process**.
     Rev 3's retirement of `cato` stands. Worth knowing the field is not *always* wrong.
7. **SAFETY DEFECT, UNOWNED — `pkill -f` is inconsistent across sessions. Escalated to the operator by
   `auden` at rev 4 with an owner request; not resolved.** `barr` had it **refused** as shadowed, with
   the explanation that `-f` matches full command lines including `--append-system-prompt`, so a broad
   pattern would SIGKILL sibling Claude sessions. `soren` then ran
   `pkill -f "GradleDaemon.*SnaKt-wip13"` and it **succeeded**. Same bot, same day, two solver sessions,
   opposite behaviour. **This is worse than no guardrail**, because `ennio` had propagated "it is
   refused" to five solvers as a reassurance they might have acted on. `ennio` retracted it promptly;
   the standing rule is now **"never pattern-kill; it may succeed."** Keep that rule regardless of how
   the inconsistency is resolved. Second-order consequence: `soren`'s kill is a **second candidate
   cause** for `barr`'s daemon death, so that death is not evidence about `barr`'s own change.
8. **Context limits are `CONTEXT_WARN_TOKENS` 150,000 / crit 200,000** (`ennio`, rev 4). Get work
   committed and spawn fresh rather than driving past the warn line. Trust the turn footer, not your
   sense of your own size: `chime` parked at 143k while estimating itself at 100k.

## Defects in earlier revisions — stated so they are not re-inherited

- Rev 1 called `solvers.zip` "Step 5's bundle nested inside `strategist.zip`". It is **Step 4's**. The
  staged file was correct, so no work was lost, but a seat holding it as Step 5's would withhold the
  exact bundle Step 4 needs while hunting for another. This seat repeated the mislabelling unchecked
  before vivian corrected it.
- Rev 1 did not name the 2c worker (`tobias`), so its liveness looked unverifiable and briefly like an
  orphaned spawn. Hence the instance table above.
- Rev 1's `./gradlew build` claim was broader than the truth (the `test` path is fine).
- Rev 2 recorded a **guess** as an expected resolution ("synthesizer nested in `solvers.zip`, so the
  dist question dissolves") and recorded "159" with no command. Both removed. **Do not leave your own
  unchecked expectations in this file** — that class of thing produced the false-green, the Step-5
  mislabelling, and the guard overstatement.
- Rev 3 gave `synthesizer.zip` as 1978 bytes without saying **which** size. It is 1104 compressed / 1978
  uncompressed — a units mislabel originating with the Launcher, not chime. **Rev 4 initially recorded
  this as a transcription error by chime; that was wrong and is retracted here.** Two seats in a row
  carried a number without its measurement.
- Rev 3 described orchestrator-x as having "a different role set". It is a **superset** containing our
  whole spine in order; four extra roles, none missing. Overstating the mismatch made the relay look
  more doubtful than the evidence supports.
- Role files say to "pass through the feature repo path", but a Strategist's own read "your own
  allocated worktree", which taken literally points the next agent at the previous agent's tree —
  wrong, since worktrees share a ref store. `spawn --repo <base>` allocating fresh is what prevents
  this, not the instruction.

## Standing constraints

- One PR for the run (#30). Commits append.
- Worktrees share one ref store: a branch checked out elsewhere cannot be checked out. Branch from
  `e1cd7c1c` under a fresh local name; push to `origin/feature/custom-predicates`.
- Do not open PRs against `JetBrains/SnaKt`.
- Spawn every worker with an explicit model. Do not drive one past ~150k context; get its work
  committed and spawn a fresh one. Brief workers to tag turn-ends `%notify: spawner` — **untagged
  turn-ends reach nobody**, and a worker that skips updates looks wedged.
- Never send a message only to say "done" or "no action needed"; it wakes the target for a wasted turn.
  Silence is how you tell a session to do nothing.
- cleo's commits lack the `Co-Authored-By` trailer; it declined to force-push history the run depends
  on for cosmetic metadata. Agreed, left as is.
- **NEVER use `git stash` in this repo — `refs/stash` is a single ref shared by every worktree, not
  per-worktree.** Rev 5. `zara` stashed for a clean baseline, `saskia` stashed onto the same stack in
  the interim, and `zara`'s `pop` took `stash@{0}` — **saskia's** — consuming and dropping it and
  applying saskia's files into zara's tree. Fully recovered and byte-verified (all four files identical
  out of the object store), but only because `zara` copied the content aside and **reported** it instead
  of quietly cleaning up. Use `mv` into `/tmp` for baseline isolation. This is a standing constraint,
  not a Step-4 ruling: it outlives the iteration. The deeper protection is the `[UNVERIFIED]` push
  convention — **a pushed commit cannot be eaten by another worktree's stash pop.**
- **Derive a load-bearing total two ways.** Rev 5. Two seats independently hit the same trap in one
  hour: `grep -c '@Test'` also matches `@TestMetadata` and `@TestDataPath`. Both were caught by a sum
  refusing to reconcile, not by suspicion — so reconciliation, not care, is what catches it.
- **Read a peer's context size from the instrument, never from its self-report.**
  `/opt/bots/log/cost-watcher/<bot>/<persona>/<session_id>-live.json`, field `last_ctx_tokens`; check
  `updated_ms` for staleness. `intersession peek` does **not** surface this field, so a session can
  believe it has no such file when it does. Rev 5: `sacharissa` reported it could not measure itself and
  asked to be replaced on judgement; the instrument read **134,681**, 60 s old, and the handover was
  decided on that. **Third self-report about context to be wrong in this run, in both directions, and
  the first resolved by external measurement rather than by arbitrating between two senses of it.**
- **The committed test runner must stay in sync with `testData` — one equality, checkable with no
  build.** Rev 5. `PhasedDiagnosticTestGenerated.java` is **generated but tracked and not ignored**, and
  upstream keeps it exactly in step: `bf32366c` 111 `runTest` ↔ 111 `.kt`; `e1cd7c1c` 116 ↔ 116. At
  feature tip `4ead163e` it is **116 ↔ 137, broken by 21** — the unwired files. So
  **`committed runTest count == committed testData .kt count`** is a cheap positive gate condition, and
  it beats grepping the runner for file names. **Ruling: no solver commits the runner. Exactly one
  regeneration commit at aggregation**, after all test files are on the feature branch; six solvers
  regenerating one tracked file is six divergent versions of it, and a generated file is deterministic
  from its inputs so a merge buys nothing one regeneration does not. This promotes rev 1 §10's "take the
  incoming version and re-run `generateTests`" from a note to the procedure.
- **A negative case is not satisfied by failing. It is satisfied by failing for the stated reason.**
  Rev 5. **Require the diagnostic text, not the exit status.** A rejection on a permission ground where
  a predicate ground was expected is a **false negative control that reads as success** — and since the
  controls already cannot discriminate predicate *body content*, this is the one remaining place a
  control could quietly prove nothing.
- **Resolve an ambiguous rule as structural, not hygiene.** Rev 5, the Launcher. The two errors are not
  symmetric: **a hygiene rule mistaken for structural costs time; a structural rule mistaken for hygiene
  costs the result.** Label every rule you hand a subordinate as one or the other — `sacharissa`
  mislabelled one in each direction and a successor cannot recover the distinction from the text alone.

### A distinct defect class — a fix landing where the error was noticed, not where it propagated

Rev 5. Separate from the measurement-provenance class, and it has now fired **three times**:

1. The `@Manual` retraction lived in the Method B ruling and the dispatcher state, but **not** in
   `testing/custom-predicates-strategy.md` §2, which is the document solvers actually read. Fixed at rev 5.
2. `Builtins.kt`'s KDoc for `predicate` demonstrates `next!!.sorted()`, and **`!!` cannot compile
   plugin-wide** (`FirCheckNotNullCall` is "Not yet implemented"). The Launcher seeded it in the feature
   brief without checking `!!` was supported; the Implementer **fixed it in the test code and nobody
   went back to the doc comment**. At `4ead163e` `!!` appears **three times** in that KDoc — twice in the
   prose explaining why the `Boolean` return type permits self-reference, once in the worked example. So
   the explanation of the feature's central design decision is written in syntax the plugin rejects, and
   a user fails twice for one reason. In PR 30 as its own section; deliberately not fixed here (six
   concurrent authors in one file).
3. This state file's own rulings, which is why they are being written into the plan and strategy docs
   rather than left in messages.

**The rule: when you correct something, fix it at the source and everywhere it propagated, not at the
site where you noticed it.** Ask what *else* reads the thing you just found wrong.

### Another distinct class — over-retraction: withdrawing a whole claim when one part of it failed

Rev 5, self-disclosed by the Launcher, and **the first instance cost the run a real finding for two
turns.** The unwired-runner alarm had two halves:

- **Observation:** the committed runner contains no entries for the new test files. **True.**
- **Conclusion:** so the tests silently do not run. **False** — `compileTestKotlin dependsOn
  generateTests`, and `generateTests` declares `testData` as an input, so it regenerates before compile.

The whole claim was withdrawn when only the conclusion was wrong. **The accurate position was never
"refuted" but "the conclusion is refuted and the observation is a real defect of a different kind"** —
and it is a worse defect than cosmetic, because it breaks the `runTest`-count/`.kt`-count equality that
holds at every other commit in the repo. Recovering it took a second seat re-deriving it from the other
direction.

**The tell was available at the time, and it is checkable without knowing which half is wrong: count the
claims you made, then count the ones the correction addressed, and dispose of the remainder.** A two-part
claim with one part refuted leaves the other part **still requiring disposal** — the Launcher did not
dispose of it, it dropped it.

**This is a provenance error in the same family as accepting a number without its command:** in both
cases a claim and its scope have come apart. Cf. `auden` overcorrecting its context estimate after being
caught underestimating. **A correction is information about one specific claim, not licence to swing.**
When you retract, state which half failed and what survives.

## Step 4 status at rev 4 close — the zero, and why its shape matters

**Verified gates: ZERO.** Reported flat by `sacharissa`. All six solvers are category (a) — nothing. No
category (b) either, so **no fresh exit-144 sighting**.

**The zero is about provenance, not absence of work, and this is the run's discipline finally working
prospectively rather than retrospectively.** A FULL gate on `soren` **ran to completion** while
`sacharissa` was answering — its `flock` released and it re-queued. `sacharissa` still reported zero,
because the log lives in `soren`'s tree, which it cannot read, and `soren` had not reported it. **Counting
a completed run whose output has not arrived is exactly the remembered green section 0 forbids.** Every
false green in this run was caught downstream, after adoption; this is the first time the rule stopped one
*before* it entered the record.

Pipeline is turning over at roughly **one gate per seven minutes**, one invocation per solver, all six
accounted for.

**The livelock alarm was revised down and then REVERSED — take the reversal.** `sacharissa` first
de-escalated it on ~7 min/run arithmetic, then reversed itself when `barr`'s real gate came in at
**11m39s**, roughly double. Recomputed, `zara`'s acquire lands near **50–57 minutes** against `-w 3600`.
Two further facts it added:
- **`flock` offers NO FAIRNESS GUARANTEE.** Queue position is not FIFO and a waiter can be overtaken
  indefinitely. **The tail is not merely slow, it is unbounded in principle.**
- Therefore **`-E 75` plus the two-re-run budget is not belt-and-braces — it is the load-bearing half**,
  because it is the only thing making an unbounded wait bounded and reportable.
- `barr`'s 11m39s is at the **baseline 136 cases**. Runs carrying new Silicon-invoking cases will be
  **slower still**.

`zara` has been warned specifically that its queued run may time out without ever reaching gradle, and
that this is **not a result**.

### A Planner ruling superseded by a solver, RATIFIED — do not reverse it

`barr` dropped the step §6 explicitly endorsed: running the four-case set first as "the control for its
controls". `sacharissa` ratified rather than reversed it and flagged it upward. **`auden` ratifies it too,
on the way out, so `remo` inherits a decision rather than an open question.**

The reasoning is sound and I accept it against my own earlier ruling: `barr`'s revised three-case set
**carries the same forwarding shape** (`flushTwice`, `discountTwice`, `consumeBothTwice`), so **one run
answers whether forwarding verifies at all *and* the access-presence question together**. The original
sequencing bought no additional information for three more lock slots, and **on this host lock slots are
the binding constraint** — which the 11m39s figure and the no-fairness fact make sharper, not weaker.
`sacharissa` was also right to decide it while the window was open rather than wait for a ruling.

**The endorsed intent survives; only the sequencing changed.** Nothing must be read as verified before a
run establishes that the forwarding shape verifies at all.

**Pre-labelled race, `barr`'s own disclosure:** it swapped files while `generateTests` was queued, so if
that task enumerated `testData` before the `git rm`, the runner will reference the deleted
`custom_predicates_b1_internal_invariant` and fail **for that reason alone**. **If that name appears in a
failure it is the race, not a result.** `barr` will verify runner entries against the on-disk set and
re-run `generateTests`.

**Solvers were sitting on uncommitted deliverables.** `sacharissa` caught `barr`'s revised three-case set
and `git rm` existing **only in its working tree**, with its branch tip still at the four-case
`7d3a216f` — the actual Method B deliverable, unprotected. Same for `zara`, whose `solve/solver-a-2` still
read `e1cd7c1c`. Both instructed to commit as `[UNVERIFIED]` and push first. **This is why the
`[UNVERIFIED]` convention matters beyond labelling: it removes the reason a solver sits on uncertain
work.**

### `./gradlew` PRINTS NO TEST COUNTS ON SUCCESS — rev 4, and it reaches back

`barr`'s successful log ends `BUILD SUCCESSFUL in 11m 39s` plus a task summary and **no counts at all**.
The `tests=136 failures=0 errors=0 skipped=0` figure was obtained by **aggregating the JUnit XML** under
`formver.compiler-plugin/build/test-results/test/`.

**So "136 / 0 / 0" can never have come from a success log, and every count in this file must now say
whether it came from JUnit XML or from a log line.** That includes rev 3's counts, which are recorded with
their commands but **not with their extraction method** — they are not thereby wrong, but they are
under-specified in exactly the way this run keeps being bitten by. **Sixth instance of the
measurement-provenance class.** State the extraction method alongside the command from here on.

**Instrument fault, self-disclosed (`sacharissa`).** Its first monitor counted `flock` processes with
`ps | grep -c`, and **the grep's own pattern matched itself**, inflating the count by two — it would have
fired early. Stopped with `TaskStop`, never a pattern-kill, and rebuilt to resolve worktrees via
`/proc/PID/cwd`. The earlier "six waiters" was **right by luck, not by measurement** — the same
count-without-its-command class, hit inside the tooling rather than the build. **Third self-caught error
in the run**, after `ennio`'s breach disclosure and this seat's `@Manual` retraction; all three arrived
with the remediation attached.

**Guard datum, incidental:** `sacharissa`'s six-solver broadcast quoted both the lock path and the main
checkout and **none were refused**, so the guard is about *forbidden* paths specifically, not paths in
general.

**Convention in force:** keep `barr`'s `[UNVERIFIED]` subject-line tag on every solver commit until its
gate passes. A commit subject stating its own verification status **cannot be misread by someone scanning
`git log`**, which is exactly how a green gets adopted here. `7d3a216f` verified off the shared object
store by the Launcher: four sources, 124 insertions, tag intact, feature branch still `e1cd7c1c`.

**Launcher's standing request:** the **first** gate that lands must be reported with **its command line and
its captured output**, not its result — it is the artefact that breaks a chain of four false greens and must
enter the record checkable by someone who trusts nobody. After the first, results are fine.

## Step 4 in flight — findings so far

First solver to report is `barr` (solver-B-1); five still working. Solver names live with `ennio`.

**Host capacity: `-Xmx6g` and parallel solvers are incompatible on this box.** `barr` lost a daemon
("Gradle build daemon disappeared unexpectedly"). Verified cause: the host has 11 GB total with ~3 GB
available and six daemons live, so six concurrent `-Xmx6g` runs demanded 36 GB. **Keep `-Xmx6g`** — peak
RSS in a single run was ~1.5 GB, so the ceiling was never the problem, concurrency was. ennio has
instructed all six to wrap every gradle invocation in `flock -w 3600 /tmp/snakt-gradle.lock`, which
serializes host-wide while keeping the 6g ceiling. Cost: solvers block on the lock and iter-1 wall-clock
stretches. **Worth carrying as a fleet rule.**

**OPEN DECISION for this seat and the Strategist — Method B may have been weakened.** `barr` found that
all four Method B sketches want the predicate's invariant *consumed by a method body*, and argues no such
program shape exists: A13 havocs `var` reads, and a `val` read embeds as a permission-free Viper function
Viper cannot see into without an unfold the plugin never emits. So `verify(p.v <= 100)` under
`acc(valid(p))` fails for **both** property kinds — which is exactly the accepted non-coverage already
pinned in `custom_predicates.viper.diag.txt`. `barr` reshaped all four to declaration + consumer
requiring the predicate + caller forwarding it, since predicate-to-predicate forwarding is what actually
verifies; case 2 became `!loggedIn || user != null` with both properties `val`, there being no `!!`.
Committed as `[UNVERIFIED]` pending its gate run (full `:test` for all four).

ennio did not overrule the reshape — the alternative was four cases doomed for an unrelated reason — but
flagged the consequence, correctly: **Method B no longer demonstrates "a predicate expresses an invariant
Kotlin's type system cannot, and the invariant is then used". It now demonstrates the weaker "the
invariant can be stated and forwarded."** Whether that weakens Method B past its purpose is a
Planner/Strategist call. Decide it before synthesis rather than letting it be discovered there. Given
`vivian`'s ~123k, spawn a **fresh** strategist on the Step-3 documents to rule on it.

## Step 4 status at rev 5 close — `remo`'s seat

**Verified gates on solver work: ZERO**, after eight askings. Correct, not idle. Every zero in this run
has been a refusal to count something whose output had not arrived, and each refusal was right.

**Live instances at rev 5:** `niamh` Launcher. `remo` Planner (this seat, replaced `auden`).
**`rune`** Step 4 Solver Dispatcher on opus, **replacing `sacharissa`** — spawned from
`.dispatcher-replacement-briefing-2.md` after `sacharissa` passed 170k. Six solvers re-rooted onto
`rune`: `barr` (b-1), `saskia` (b-2), `soren` (n-1), `briar` (n-2), `zara` (a-1), `felix`.
`sacharissa`'s state file is
`testing/custom-predicates-step-4-dispatcher-state-iter-1-rev2.md`, **675 lines, md5
`1995b719c357af6d94ac32a08e488269`** — a **delta on rev 1**, not a replacement. **Do not cite the
603-line / `41f0f8f1…` figure**: it was accurate when verified and stale when read, and it went into
`rune`'s briefing that way.

**Solver contexts, from the instrument:** `briar` **144k — replace, do not drive**; `zara` 131,328;
`soren` 112k; `barr` 98k.

### The one generalisable finding of this iteration — a gate specified before its failure

Every other control in this run was written **in response** to something that had already gone wrong:
the FULL-mode correction, the `LOCK-ACQUIRED` marker, the golden-diff requirement, the `[UNVERIFIED]`
tag, the count-with-extraction rule, condition 3. **The runner sync invariant was read off the repo's
own history and then immediately earned its keep** — and then did better than that:

1. It caught the 21 unwired files **prospectively**.
2. The Planner's ruling on *how to fix that* was ambiguous and **caused a second defect** (a churn cycle
   of three commits).
3. **The invariant caught that one too** — off the committed tree, no build, one command.

**A gate that keeps working when the procedure wrapped around it is wrong is a different order of thing
from a cheap gate.** The lesson: **the repo's existing consistency properties are a source of gates, and
they cost nothing to check.** Look for equalities the history already maintains.

### The counting identity — settled, and the offset that burned three seats

    committed runTest count  ==  committed testData .kt count        <- the INVARIANT pair
    runTest count + 20 testAllFilesPresentIn* methods == total tests <- the GATE COUNT

**Two patterns, two purposes; conflating them is the error.** `grep -c 'runTest('` for the invariant;
`grep -c 'public void test'` for the total. Confirmed independently on three branches: `barr` 119/139,
`soren` 125/145, feature tree 137/157. **`grep -c '@Test'` is wrong in both roles** — it also matches
`@TestMetadata` and `@TestDataPath`, giving 292 in this seat and 298 against 139 real tests for `barr`.
Off by more than double is implausible enough to self-refute; **the near-miss `public void test` used in
the wrong role is the dangerous one.**

**157 is a POST-AGGREGATION figure. It is not a current target.** While the branch sits at 116/137 a
FULL gate correctly reports **136**. This correction reached `sacharissa` minutes before its successor
was seated; later, and `rune` would have inherited 137 as a target and read a correct 136 as failure.

### Branch state — churn cycle, and the merge gate that came out of it

    e1cd7c1c  last gated tip                        116 runTest / 116 .kt   invariant OK
    4be0c15a  zara,   5 Method A test sources
    e10bcf18  saskia, 4 Method B (stash-recovered)
    4ead163e  briar, 12 Method N (incl. 3 N4)       116 / 137   BROKEN
    1f38d14a  partial regen, a2 only                121 / 137   still broken
    6d2e0518  full regen                            137 / 137   restored, total 157
    458cb00f  revert of both                        116 / 137   BROKEN — current tip

`barr`, `felix`, `soren` are on `origin/solve/solver-{b-1,a-1,n-1}` — `barr` deliberately holding its
feature-branch push rather than rebase a tree its queued runs execute against. **All six have pushed;
nothing is stranded in a working tree.**

**The revert is CORRECT, and the Planner's original ruling gave it the wrong justification.** "No solver
commits the runner" was read as "the runner must not be committed". The real reason the revert is right:
**137 is not the final count**, since `barr`'s 3 plus `felix`'s and `soren`'s files are still on slug
branches, so any regeneration now is superseded. **A solver acting correctly on a wrong rationale is
indistinguishable in the log from a solver acting incorrectly — which is why the justification must be in
the file, not only in the instruction.**

**Corrected ruling: regenerate exactly once, LAST, after the final test file lands.** The branch may sit
inconsistent meanwhile; the build self-heals via `compileTestKotlin dependsOn generateTests`. **The
hazard is inverted from what was first flagged: a partial regeneration is visibly wrong and gets
superseded; an ABSENT one is invisible and merges.** So it is a **merge gate owned by the Launcher, not
an authoring task.** Nothing merges until the equality reads N/N. Solvers: leave it dirty, say so, never
commit a partial regeneration, **and never revert someone else's.**

### Two ways to misread an absence, both found within minutes of the rules that created them

1. **A mid-run snapshot looks like a finished gate.** The artefact-preservation rule (copy gate output
   into `temp/gates/<solver>-<ts>/` so it survives the seat) worked, but `briar`'s copy came back with
   its update run **still in progress**. Those 21 XML files are not gate evidence. Repeat the copy on
   completion; it is cheap.
2. **An absent or empty `build/test-results/test/` is the signature of a JVM death, not evidence of zero
   tests.** `soren`'s daemon died with `LOCK-ACQUIRED` present and **no `BUILD` line**, so JUnit never
   flushed. Exact opposite failure from the blended-stale-XML problem, and it must never read as a clean
   run.

**Substantive result, deliberately NOT a gate:** `soren`'s dead pass produced the N2 golden before dying
— both unembeddable shapes diagnosed per contract, plus the finding that **`String` lands in the same
class as a type parameter because the plugin has no class embedding for it.** Intended gate matched
observed. **No `BUILD` line, so it is a confirmed case, not a verified gate.**

### Standing practice for this seat

**Write the state file incrementally, as you go, not at handover.** Rev 5 was amended continuously
rather than saved up. Three seats before this one — `chime`, `ennio`, `sacharissa` — each had to be
sequenced carefully at handover **precisely because their state was owed rather than already written**.
A state file written as you go cannot be lost by the seat that owes it.

## Immediate next actions for the incoming seat

1. **Dispatch Step 5 from `synthesizer.zip` once ennio's iteration lands** — the operator has ruled it
   usable despite being cross-generation. Record the provenance and the ruling in the step doc, and
   report any Run Context mismatch rather than inventing field values.
0. **Rule on the Method B weakening** (see "Step 4 in flight") before synthesis, with a fresh
   strategist. **Seated at rev 4:** `otto`, fresh Strategist on opus, is writing
   `testing/custom-predicates-method-b-ruling.md`. **SETTLED at rev 4** — Planner decision in
   `testing/custom-predicates-method-b-planner-ruling.md`, sent to `ennio` for `barr`. Method B is not
   weakened past its purpose (forwarding *is* the whole available semantics, since `Stmt.Fold` is
   constructed nowhere), but the reshaped cases are **vacuous** — every predicate access is assumed,
   never proved, so each passes whether Viper attends to the predicate or not. Fix: **negative controls**,
   no fifth case. **N5 struck** as not expressible. One departure from `otto` recorded there: its
   `:untilConversion`-only gate for cases 2 and 4 is **declined**; they merge into one FULL-gated case.
   `vivian` was deliberately not driven into this.
   - **A retraction inside that ruling, and it is the run's signature error a fourth time.** The ruling
     first claimed a `@Manual var` read verifies in a method body, citing
     `testData/diagnostics/verification/classes/acc_precondition.kt`. That file is `// NEVER_VALIDATE`
     (line 2) → `conversionOnly` (`ExtensionRegistrarConfigurator.kt:49`) → `FORCE_DISABLE` (`:54`), so
     **Viper never runs on it**; every marker in it is `<!VIPER_TEXT!>`. It also has no `var` read in a
     method body at all — the bodies are writes, and the `read()` variant's body is empty. `ennio` caught
     it after it had reached five solvers. **`@Manual` is a real third `accessPolicy`, and whether a
     `@Manual var` read verifies is untested in either direction.** §2 should say *"default policies
     havoc; `@Manual` is untested."* A conversion-only artifact was read as verification evidence inside
     a correction to the strategy doc — where it would have hardened permanently.
   - **Accepted non-coverage from `barr`, recorded so a green control is not over-read:** the negative
     controls discriminate predicate-access **presence**, not **content**. A caller omitting the
     precondition fails to supply `acc(P(x))` whatever `P`'s body says. Presence hole closes, body-content
     hole stays open; with no `fold` nobody has a body-content discriminator. **A passing control is not
     evidence that Viper attends to the predicate body.**
2. Monitor for `complete/custom-predicates-step-4-iter-1.md`. If ennio dies before writing it, apply the
   Respawn Protocol → `salvage/custom-predicates-step-4-iter-1.md`.
3. Collect the six solvers' **verbatim role-file relay quotes** from ennio when they report.
4. If the strategy needs revisiting, spawn a **fresh** strategist on the Step-3 documents rather than
   driving `vivian` (~123k).
5. Ask the Launcher for a snapshot after amending this file.

