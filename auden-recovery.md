# Recovery fragment — `auden` (Planner/relay, rev 4 seat), custom-predicates run

Written after the artifact root was found empty at ~17:20 and `artifacts-history` was confirmed gone with
it (it lived inside the root it snapshotted). Pushed to a non-branch ref on `origin` because that is the
one preservation mechanism that survived the day.

**This is not a substitute for the transcripts.** `/home/silverbot/.claude/projects/` persists outside the
root and holds every artifact file's content verbatim, since they were written through tool calls. Use
those as primary sources. This file holds only what is either (a) uniquely mine and unrecorded elsewhere,
or (b) an edit made after the last snapshot `c273ad4`.

## 1. PRIMARY RECORD — the `pkill` sightings

The Launcher named this the one item it let expire. Both sightings reached this seat directly from `ennio`.
Marked by what is verbatim and what is not, because by this run's own standard the captured command text
is the load-bearing datum, not the outcome.

- **Sighting 1 — `barr`, REFUSED. Command and refusal string NOT held.** `ennio` relayed the *explanation*
  only: `pkill -f` is "shadowed in agent sessions", because `-f` matches full command lines including
  `--append-system-prompt`, so a broad pattern silently SIGKILLs sibling Claude sessions. Nothing was
  killed. **Do not present this as a verified refusal** — the raw text is missing. Only `barr` can supply
  it. **That is the single outstanding datum**, and verbatim quotes are the only thing that has settled a
  question on this run.
- **Sighting 2 — `soren`, SUCCEEDED. Command held verbatim:** `pkill -f "GradleDaemon.*SnaKt-wip13"` — its
  **own** worktree, not a peer's. Executed, returned successfully, not refused.
- **No third sighting exists.** Two, same bot, same day, opposite outcomes.
- **REFRAME, offered as inference: these may be TWO MECHANISMS, not one inconsistent guard.** Sighting 1
  was described as a **shadow on the `pkill` tool**; the write guard produces a different refusal shape
  ("Refusing: this writes to … outside your working directory"). A tool shadow with a gap is a different
  fix and a different owner than a guard firing inconsistently. Supporting it: the write guard
  pattern-matches raw command text, yet `soren`'s pattern carried a worktree slug **inside a quoted
  string** and was not refused — so the write guard was plainly not what stopped `barr`.
- **What holds either way, and is the part to keep: never pattern-kill; it may succeed.** `ennio` had
  turned "it is refused" into a reassurance to five solvers before `soren` refuted it, which is why it is
  worse than no guardrail.
- Consequence: `soren`'s kill is a **second candidate cause** for `barr`'s daemon death, so that death is
  **uninformative** about `barr`'s own change. A real loss of evidence, not merely an unknown.

## 2. Edits made after the last snapshot `c273ad4` — re-apply these

### 2a. Publish checksums only for frozen artifacts

`ennio` amended its dispatcher state file after handover, so the md5 this seat circulated
(`d3b68627a20c08633da9640121d3e0ec`, 362 lines, 21588 bytes) went stale; the file became
`f8b2420d2259d23363aab870d80232fe`, 374 lines, 22522 bytes.

**Rule: publish a checksum only for a FROZEN artifact (a bundle). For a living state file the path is the
identifier, not the hash.** The note was accurate when written and became wrong through someone else's
legitimate edit. The discipline built to catch false artifacts is exactly what would turn that into a
false alarm — a conscientious successor gets a mismatch and concludes it holds the wrong file. **A
verification practice that manufactures its own false positives spends the trust it was built to create.**
Confirmed independently across three seats (`auden`, `niamh`→`annie`, `remo`→`rune`).

### 2b. RE-ARM A MONITOR

`ennio`'s monitor expired. This is **work-destruction risk, not re-run risk**: an OOM would silently
destroy uncommitted solver work, and `sacharissa` had just caught `barr` and `zara` holding deliverables
that existed **only in their working trees**. The Launcher measured four of five queued runs carrying
`-Pkotlin.test.update.test.data=true`, i.e. **golden-update mode** — the worst window for an unmonitored
OOM, because a pass dying mid-write leaves **partially-written goldens** rather than none, and the next
pass's baseline reads them. **That is the golden-update trap and the contaminated-baseline trap composing
into something neither is alone.** Confirm a monitor is armed **by observation**, never by assumption.

### 2c. A Planner ruling superseded by a solver — RATIFIED, do not reverse

`barr` dropped the step §6 endorsed (running the four-case set first as "the control for its controls").
`sacharissa` ratified and flagged it; **`auden` ratified it too.** Reasoning accepted against this seat's
own earlier ruling: `barr`'s revised three-case set carries the **same forwarding shape** (`flushTwice`,
`discountTwice`, `consumeBothTwice`), so **one run answers whether forwarding verifies at all *and* the
access-presence question together**. The original sequencing bought no information for three more lock
slots, and on this host **lock slots are the binding constraint**. **The endorsed intent survives; only the
sequencing changed** — nothing counts as verified before a run establishes the forwarding shape verifies at
all.

Related, `barr`'s own pre-labelled race: it swapped files while `generateTests` was queued, so if that task
enumerated `testData` before the `git rm`, the runner references the deleted
`custom_predicates_b1_internal_invariant` and fails **for that reason alone**. **If that name appears in a
failure it is the race, not a result.**

### 2d. `./gradlew` PRINTS NO TEST COUNTS ON SUCCESS

`barr`'s successful log ends `BUILD SUCCESSFUL in 11m 39s` plus a task summary and **no counts at all**.
The `tests=136 failures=0 errors=0 skipped=0` figure came from **aggregating the JUnit XML** under
`formver.compiler-plugin/build/test-results/test/`.

**So "136 / 0 / 0" can never have come from a success log, and every count must state its extraction method
alongside its command.** Earlier revisions recorded counts with their commands but **not their extraction
method** — not wrong, but under-specified in exactly the way this run keeps being bitten by.

### 2e. Lock arithmetic — take the REVERSAL, not the revision

`sacharissa` first de-escalated the livelock hazard on ~7 min/run arithmetic, then **reversed itself** when
`barr`'s real gate came in at **11m39s**, roughly double, putting `zara`'s acquire near **50–57 minutes**
against `-w 3600`. Two further facts:
- **`flock` offers NO FAIRNESS GUARANTEE.** Queue position is not FIFO; a waiter can be overtaken
  indefinitely. **The tail is not merely slow, it is unbounded in principle.**
- Therefore **`-E 75` plus a two-attempt re-run budget is the load-bearing half, not belt-and-braces** — it
  is the only thing making an unbounded wait bounded and reportable. This inverts the priority this seat
  originally gave.
- `barr`'s 11m39s is at the **baseline 136 cases**; runs carrying new Silicon-invoking cases are slower.

### 2f. The livelock discriminator (defect in guidance this seat wrote)

"No `BUILD` line → non-result → re-run" conflates two causes with **opposite correct responses**, and as
written it livelocks: a `flock` timeout also exits non-zero with no `BUILD` line, and re-running re-queues
at the back of a saturated lock. Discriminator, textual rather than exit-code-based:

    flock -w 3600 -E 75 /tmp/snakt-gradle.lock bash -c 'echo LOCK-ACQUIRED-$(date -Is); ./gradlew …'

**No `LOCK-ACQUIRED` line → lock never obtained**, re-run is correct and cheap (max two attempts).
**`LOCK-ACQUIRED` present but no `BUILD` line → the real unexplained exit-144 class**, which must be
reported with its log, never silently re-run. A `flock` timeout emits no gradle output at all; the
exit-144 sightings had ~2 lines ending `Reusing configuration cache`, so they had already entered gradle.

## 3. Verified state at the moment of loss

- **Verified gates on solver work: ZERO.** Reported flat by `sacharissa` *while a FULL gate on `soren` was
  completing* — it declined to count a completed run whose log had not reached it. **The zero was about
  provenance, not absence of work.** First time in the run the rule stopped a false green *before* it
  entered the record rather than after.
- `origin/feature/custom-predicates` at `e1cd7c1c`. `barr` at `7d3a216f`, `[UNVERIFIED]`, four test
  sources, 124 insertions. Per-solver branch slugs, which sidesteps the generated-runner conflict.
- `complete/custom-predicates-step-4-iter-1.md` **correctly unwritten** — the step is not done.
- Six solvers: `barr`, `saskia`, `soren`, `briar`, `zara`, `felix`.
- **Convention in force:** `[UNVERIFIED]` in the commit subject until the gate passes. It is what makes
  committing uncertain work safe, so it removes the reason a solver sits on it.

## 4. Recovery routes verified from this seat

- **Bundles: intact.** `/home/silverbot/data/received/` is outside the root — `orchestrator-x/agents/`
  holds all nine bundles including `synthesizer.zip` (md5 `d843046ea69f9106e4585186ce657684`, **1104 bytes
  compressed / 1978 uncompressed** — both figures correct, they measure different things), plus
  `orchestrator.zip`, `_1`, `_2`, `_3`.
- **`solvers.zip`: second independent route** — `refs/pipeline/solvers-bundle` on `origin` at
  `3db37f4433adcac33a440d49766ba4f0671a2370`, `vivian`'s orphan commit. The non-branch-ref discipline paid
  for itself.
- **Transcripts** in `/home/silverbot/.claude/projects/` hold artifact content verbatim (written through
  tool calls). **Primary sources — this makes reconstruction, not recollection.**
- **`artifacts-history` is GONE**, having lived inside the root it snapshotted. Plan no recovery around it.

## 5. Do not

- **Do not write into the artifact root.** Cause of the emptying is unknown, and the mode change from
  `drwx------` to `drwxrwxr-x` points at the allocator, so it may recur.
- Do not `git init` the artifact root.
- Do not dispatch `auditor.zip`. Do not open a subordinate's bundle.
- Step 5's `synthesizer.zip` is **not** blocked — the operator ruled proceed on the cross-generation
  artifact. It is the Planner's call, never the dispatcher's.
