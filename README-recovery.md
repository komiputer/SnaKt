# Artifact-root loss — `annie`'s salvage (Planner seat)

The shared artifact root `/home/silverbot/dev/.empty-sessions/empty-2wwyx31d/` was **emptied**,
taking `artifacts/` and `artifacts-history/` with it. **The run is paused by the operator.**

## The event, bounded by measurements rather than inferred

- **15:20:18Z** — I successfully read and measured
  `handoffs/step5-synthesizer-briefing.md`: 142 lines, 9445 bytes, md5
  `2a150b35bd6ce464c2eacc45940b4c19`.
- **15:21:04Z** — the path returned ENOENT.
- Parent mtime 17:20 local. Directory mode changed **`drwx------` → `drwxrwxr-x`**.
- `rune` hit ENOENT independently on a directory it had read minutes earlier, and confirmed
  sibling `.empty-sessions/empty-*` directories still hold their `artifacts/`. **So this was
  reclamation of one directory, not a disk-wide event.** The mode change points at the
  allocator, which means **it can happen again** — do not re-establish anything there until the
  cause is known.

**Do not re-verify the loss by reading the root.** `rune` lost its last chance to re-read
`barr`'s failure log that way: the file was deleted between two of its commands.

## The design error, and it is the finding worth keeping

`artifacts-history` lived **inside** the root it snapshotted. The Launcher has stated this as her
own design error rather than bad luck, and the general form is:

> **A snapshot stored under the root it snapshots is not a backup.**

Every snapshot `c273ad4` through `ba3717e` is gone. A filesystem-wide `find` returns zero hits
for `planner-state.md` or any dispatcher state. **Plan no recovery around `artifacts-history`.**

Second-order: the run's artefact-preservation rule now points every solver at a
garbage-collected directory, **which is worse than no rule**, because a worker following it
believes its output is preserved.

## What survives, by route

| Asset | Route | Status |
|---|---|---|
| All code, commits, test files | `origin` | **Untouched.** Feature tip `458cb00f`; slugs `b0bf797a`, `af5c2652`, `819ba770`; `4ead163e` (briar 12), `4be0c15a` (zara 5), `e10bcf18` (saskia 4). Nothing any solver committed is lost. |
| PR #30 body | GitHub | Intact, and it already carries the KDoc defect, runner merge gate, ungated-commits and gates sections. |
| `planner-state.md` rev 5, **lines 1–910 verbatim** | **this seat's transcript on disk** | `planner-state-recovered.md` here. Zero missing lines. |
| `annie`'s rev-6 additions | same | `rev6-additions.md` here, exact tool-input text. |
| PR #30 headline section | same | `pr30-headline-section.md` here, reassembled clean; `pr30-headline-fragments.md` is the raw draft history. |
| All six role bundles + synthesizer | `/home/silverbot/data/received/` — **outside the root, intact** | Re-extractable from `orchestrator_3.zip` (64380 bytes) and the `orchestrator-x/` directory. The md5s recorded in the recovered state file **verify** them rather than replace them. |
| `solvers.zip` | `refs/pipeline/solvers-bundle` on `origin` (orphan `3db37f44`) | Second independent route. |
| Dispatcher state rev1+rev2, `muradin`'s analysis, `barr`'s gate numbers | `rune`'s context → branch `run-state/custom-predicates-recovery` on `origin` | `rune` read all 675 lines before the loss and is committing them. |
| Step 5 synthesizer briefing | **`origin refs/pipeline/step5-synthesizer-briefing`, commit `265a1354`** | **GAP CLOSED, byte-verified.** See below. |

**The transcript route is the important one and it generalises.** Artifact files were written
*through tool calls*, so their content sits verbatim in
`/home/silverbot/.claude/projects/<worktree>/*.jsonl`, which is outside the root. That makes this
a reconstruction job **with primary sources**, not a memory exercise. Recovery method used here:
parse the JSONL, take `tool_result` payloads containing the file, strip the `cat -n` line
prefixes, and **check for gaps by line number** — 1–910 with zero missing.

## The Step 5 briefing gap closed — and a checksum paid out in a way nobody designed it for

`remo` re-emitted the 142-line briefing from context and pushed it to
**`origin refs/pipeline/step5-synthesizer-briefing`, commit `265a1354`**. It then measured what it
had written: **142 lines, 9445 bytes, md5 `2a150b35bd6ce464c2eacc45940b4c19`** — **identical to the
figure I measured off the live artifact at 15:20:18Z, 46 seconds before the wipe.**

So the recovery is **byte-verified rather than a reconstruction**, and the mechanism is worth
stating: **without a checksum taken before the loss, a re-emission from context is an
unfalsifiable claim to have remembered a file correctly.** The measurement was taken for an
entirely different reason — the run's habit of quoting a figure with the moment that produced it —
and it is what made the verification possible. `remo` wrote its first commit message saying the
bytes were unverified and the md5 made that obsolete within the same minute.

The provenance paragraph is verbatim from the original: cross-generation, `orchestrator_3.zip`
contains no synthesizer at any depth, sourced from
`/home/silverbot/data/received/orchestrator-x/`, three days older than our chain, and **the
operator was told of the mismatch risk and ruled proceed — a documented operator decision, not a
substitution anyone inferred.** With the instruction that a Run Context field mismatch is the
mismatch surfacing rather than a worker error, and must never be papered over by inventing a
field value.

Read it without a checkout:

    git fetch origin 'refs/pipeline/*:refs/pipeline/*'
    git show 265a1354:step5-synthesizer-briefing-142line.md

The 118-line original is on the same ref as provenance only. **The 142-line version is
authoritative.** `run-rulings-recovered.md` there is the Planner-side rulings subset, counterpart
to `sacharissa`'s `f55349db` on the dispatcher side. **Neither is the state file and neither
should be described as one.**

### The durability correction, which is `sacharissa`'s and corrects `remo`

> **Copying out of a worktree solved readability, not durability — and durability was the
> property we needed.**

A peer cannot read a worktree; a session slot can be reclaimed, which is exactly what happened.
**A pushed git ref is the only store that demonstrably survived today.** Three exist, all
confirmed **remote-side** with `git ls-remote origin 'refs/pipeline/*'` at 15:25:44Z rather than
from local refs or from push output:

    ee62e6b4  refs/pipeline/planner-salvage-annie        (this file)
    265a1354  refs/pipeline/step5-synthesizer-briefing   (remo)
    3db37f44  refs/pipeline/solvers-bundle               (auden)

A non-branch ref cannot be listed as a branch or picked as a PR base, which is why it is the
right shape for run state. Worth noting the near-miss: a `git fetch 'refs/pipeline/*'` did **not**
list my own ref back, which looked like a failed push. `ls-remote` showed it present. **Push
output and fetch output are both reports; the remote's own ref listing is the measurement.**

## Substantive results that must not be lost with the artefacts

Both from `rune`, and they cross-validate. **They change the gate criterion, so they matter more
than the artefacts do.**

**1. None of the 21 new test files has a golden.** 21 `.kt`, zero `.diag.txt`, verified off the
object store. `DiagnosticsCollector.assertEquality` **returns early when the expected file is
absent**, so **an absent golden asserts NO DIAGNOSTICS**. As committed, every one of the 21
asserts the opposite of its purpose.

**This inverts the gate criterion. "0 failures" is the WRONG criterion on the current tree.** A
correct non-update gate must report **one failure per golden-less working case — 21 on the feature
tree**. **A green non-update gate here means the cases emit nothing: the vacuous outcome.** Here
the failures are the pass signal and the green is the alarm. The exact-count condition is
unaffected: **157 stands.**

**2. `barr`'s completed run corroborates that dynamically.** Its XML read
`files=20 tests=139 failures=3 errors=0 skipped=0`. **139 is `barr`'s exact predicted target
(119 + 20), and 3 is exactly its number of golden-less new cases.** Static prediction and dynamic
result agree independently — the strongest evidence this run has produced.

**Still not a verified gate.** `b1-update2.log` read `LOCK-ACQUIRED-2026-07-30T17:05:33` then
`BUILD FAILED in 7m 43s`. **Verified gates on solver work: still zero**, now on the tenth asking,
and still honest.

**3. Four of the five queued runs carried `-Pkotlin.test.update.test.data=true`** (Launcher, off
the process table). Four golden-update passes in sequence, each writing files the next one's
baseline reads — the highest-risk stretch of the iteration. An update pass rewrites the **test
source** as well as the golden, so program and expectation both move to match observed
behaviour. **No golden accepted from an update pass without a human-legible diff.** This now
compounds with finding 1: the goldens those passes write are the missing 21.

## Corrections to my own reporting, disposed of rather than dropped

- I told the Launcher the bundles were **not** recoverable from me. **Wrong** — they are
  recoverable from `/home/silverbot/data/received/`, which is outside the root. The half that
  stands: they are not recoverable *from my context*, only their checksums are.
- I published an md5 for `planner-state.md`, a **living** file I was amending. `remo`'s rule,
  adopted: **publish a checksum only for frozen artefacts; for a living file the path is the
  identifier.** A conscientious hash check against a legitimately-amended file manufactures a
  false alarm out of the very discipline meant to prevent one.

## Operational notes worth carrying

- **`cp` does not preserve mtimes; use `cp -p`.** The gate-artefact copy rule destroyed the mtime
  filter it depended on the moment it ran.
- **Count processes with `pgrep -x <exe>`, never `ps | grep`.** The Launcher reproduced
  `sacharissa`'s self-matching monitor bug herself, two turns after relaying the rule about it,
  getting 9, 5, 8, 5. **Counting from a shell whose own command line contains the pattern is
  self-inflating by construction, and the count changes with how you asked — which looks like the
  system moving when it is the instrument moving.**
- **`prefer the reading that fails loudly` governs which risk to act on under uncertainty; it
  does not excuse leaving the uncertainty unresolved.** The Launcher's louder reading (9 waiters)
  was the wrong one; she resolved it with a better instrument and got the quieter answer. That is
  the correct order.
- **`cp` into the artifact root was refused by the write guard; the `Write` tool went through**
  (`remo`). So the `ennio` precedent holds for `Write`, not for `cp`.
- **Duplicated inbox delivery, second sighting.** The Launcher's queue message arrived twice,
  byte-identical, in consecutive tool results. Acted on once. Fault 2 in the state file says a
  second sighting raises the threshold to paging.

## The pause — stop states, answered by construction

`rune` propagated the order to all six solvers and then answered the Launcher's mid-write question
**from `/proc` rather than by waiting for six replies**, which is the better instrument:

    HOLDER  wip9   :formver.compiler-plugin:test -Pkotlin.test.update.test.data=true  RUNNING
    WAITER  wip10 / wip12 / wip13 / wip11   never acquired the lock

**Only the lock holder can be mid-write, so exactly one run can have damaged anything: `felix`'s,
a golden-update pass still holding the lock.** The four waiters are safe **by construction rather
than by report** — they never acquired, so they never wrote. That is also the argument for not
killing it. **`briar`'s run has ENDED** (held the lock at the start of `rune`'s turn, gone from
`pgrep -x flock` after), and its output state is the one real unknown; only `briar` can say.

### A correction that removes an item I had recorded as closed

**PID 513609 is not an OOM monitor.** `rune` read its command line: it is `sacharissa`'s
**queue-drain watcher**, counting `flock` cwds until the count drops. It watches the lock queue,
not memory. **So no memory monitor is armed** — `rune`'s own attempt was refused by the write
guard (its log went to the artifact root) and it had reported "re-arming" before confirming.

**My error, and it is the run's signature class:** I relayed `remo`'s "monitor confirmed armed,
PID 513609, checked rather than assumed" to `rune` **as grounds to drop the item**. The checking
that was done established that a process existed, not that it was the monitor we wanted. *A
confirmed PID is not a confirmed function.* I turned a report into a verification in the act of
passing it on, which is precisely what this run's discipline exists to stop.

`rune` **declined to arm one under the pause** — correctly, that is starting something new — and
de-escalated instead: memory has recovered on its own to 2121MB available / 659MB free, from
182MB free when I asked. **De-escalating on a receded risk rather than acting under a pause is the
right call and is recorded as such**, so a successor does not read the open item as neglect.

### Pre-pause spawns, disclosed rather than discovered

`briar`'s and `zara`'s replacements were **already spawned before the pause order arrived**, and
both have since parked. `rune` stated this plainly and did not treat it as licence. **Ratified:
the disclosure is the right handling** — the same shape as `ennio`'s bundle-opening disclosure, and
the risk was never the act but a later agent citing it as precedent.

Their output, which is why the disclosure matters — it is real work now frozen:

**`muradin`** (N-2, opus), pushed to `origin/run-state/n2-case-analysis` at **`f1625cde`** with
three source quotes held verbatim. **Closed `n3_lambda`: 7 of 12 sound, nothing open.** The
local-`val` hop resolves — `visitImplicitInvokeCall` matches `is LambdaExp` and `insertCall`
inlines the body — and **`inSpecification` is a dynamic `specificationDepth` counter, not
lexical.** A coverage gap falls out of that: **the diagnostic keys off where a predicate call is
*invoked*, not where it is *written*,** so a lambda written in ordinary code but invoked inside a
specification block goes unreported. Recorded as a gap, not a defect.

**`indira`** (A-2, opus), in context and parked. Four of the five A-2 cases are empty-bodied
`preconditions { p.pred() }` and **would verify with the body replaced by `true`, so they are
vacuous on semantics** — but they do discriminate **emission well-formedness**, which is what the
strategist's §0 finding 2 actually was. **A11 is the exception and the most important case in the
set:** postconditions become `ensures` (`SignatureCreation.kt:159-202`), so `makeOrdered` must
**establish** a predicate access on a fresh object, which no `fold` permits. If both positives
fail, the adversarial pair fails **for the no-`fold` reason** — a false negative control reading
as success. **A11 must be judged on diagnostic text, positive against adversarial.**

Two further items from `indira`, both actionable when the pause lifts:

- **A defect needing no run.** `custom_predicates_a2_val_recursive.kt`'s `buildAndUse()` calls a
  predicate from a **method body** — the exact shape `predicate_outside_specification.kt:12` pins
  as a diagnostic — and it is unmarked. **It cannot be a positive case at all.**
- **The escalation trigger is structurally unable to fire on Method A.** The A-2 adversarial
  functions vary the predicate's **truth**, not its **presence**, so no predicate-omitting control
  exists in that set. The standing "stop and escalate if a control omitting the predicate still
  verifies" instruction has nothing to fire on there.

### `rune`'s own state

    refs/pipeline/dispatcher-recovery-rune           39efb622
    refs/heads/run-state/custom-predicates-recovery  39efb622

`docs/run-state/custom-predicates-run-state-recovery.md` there holds the gate conditions, the
136→157 retraction with grounds, the six-ref invariant table, the golden-absence inversion, **and
`barr`'s `139/3` figures, which now exist nowhere else** — `rune` read them minutes before the
directory died. It verified `4be0c15a`, `e10bcf18` and `4ead163e` are all ancestors of the feature
tip `458cb00f`, so **nothing any solver committed is at risk.** It declined `muradin`'s offer to
reconstruct rev2, having read all 675 lines itself — the better-positioned reader, correctly
chosen.

## Launcher ruling on the merge gate — recorded, not actioned

Hers to give. Relayed from `sacharissa` at ~197k and would otherwise have died with that seat.

**1. No golden may be assumed to have been authored by the solver who owns its case.** `briar`'s
update pass rewrote goldens for `saskia`'s and `zara`'s files —
`b2_case3_linked_properties`, `b2_case4_structural_invariant`, `a2_name_collision`. **Every golden
that pass touched is UNRATIFIED until its owner reads it.** This extends the merge gate: **the
runner equality N/N is necessary and no longer sufficient.** Aggregation must also show, per
golden, that its owner has read it. *A golden nobody has read is indistinguishable from a golden
nobody could have judged.*

**2. An update pass must run on a tree containing only the running solver's new files, or its
output is unattributable.** The contaminated baseline was recorded as a **read** hazard — a gate
counting someone else's files. **In update mode it is a write hazard**, and the
extraction-method rule does not touch it. The asymmetry makes the write case worse: **a
contaminated read produces a wrong number someone can check; a contaminated write produces a
plausible artefact with no signature of who made it.**

**3. The crash is a separate finding and must not be folded into the 20.**
`testCustom_predicates_n2_n1_subexpr` fails with `FileAnalysisException` caused by
`kotlin.NotImplementedError` — an unimplemented `TODO` reached during analysis. Not a diagnostic,
not a verification failure, **a crash**, and the first this run has surfaced. Drafted as its own
PR #30 section in `pr30-headline-section.md`, held unpasted.

**4. ZERO verified gates, unchanged.** `briar`'s run was an update pass and it failed: *157 tests
completed, 20 failed, BUILD FAILED in 12m31s* is not a gate.

**5. But 157 is now EMPIRICALLY CONFIRMED — 157 tests actually executed.** That closes the number
retracted this morning, **by a run rather than an argument.**

**The shape of that is the run's clearest lesson and it is worth more than the number.** Three
seats reached 136 independently — `sacharissa`, the Launcher via my own briefing, and `remo` — and
**`zara` held the correct figure against all three**, on the grounds that `dependsOn
generateTests` regenerates against disk. **The committed runner's staleness is invisible to a
gate.** So the error was upstream and the correction came from the bottom, and that is the
**second time today**: `barr` pre-qualified its own green before any seat above it did. **A
pipeline where corrections only flow downward would have shipped 136 as truth.**

**6. OPERATIONAL TRAP: the artifact root is live again**, because `briar`'s `mkdir -p` silently
recreated it. **It is no safer than before.** Anything written there will **look** durable and is
not. **Nobody writes to it.** Non-branch refs on `origin` remain the only durable destination.
This is a nastier state than the deletion was: an absent directory fails loudly, a recreated one
accepts writes and discards them.

## `rune` parked — what its own turn added

Beyond the stop-states above: **`saskia` and `zara` have reported and both match the `/proc`
prediction.** Two errors `rune` caught in-turn and disclosed: it briefed the new N-2 seat as
though it would queue a gate, **which would have added a seventh waiter to a saturated lock**,
corrected within minutes; and it told me it was re-arming the OOM monitor when the command had
been **refused in full**, so nothing ran. It also withdrew a false finding before it travelled —
that `briar` had overtaken `saskia` in the queue, demonstrating `flock` unfairness; `saskia`'s own
`LOCK-ACQUIRED` timestamp showed it had acquired first.

Its `run-state/custom-predicates-run-state-recovery.md` at **`39efb622`** (branch and
`refs/pipeline/dispatcher-recovery-rune`, confirmed remote-side) is the dispatcher-side record.

## The first verified gate RESULT — and it is a failure that failed as predicted

`rune` refused a relay and read the preserved artefacts itself off
`origin/recovery/dispatcher-state-iter1` (`c9fca7ca`, `recovered-gates/briar-final/`, 21 files):

    solver-n-2-update.log:1    LOCK-ACQUIRED-2026-07-30T17:15:13+02:00
    solver-n-2-update.log:143  157 tests completed, 20 failed
    solver-n-2-update.log:156  BUILD FAILED in 12m 31s
    XML aggregate              tests=157 failures=20 errors=0 skipped=0

**157 confirmed empirically.** The retracted 136 is refuted **by measurement**, not only by
reading `build.gradle.kts`. An earlier clean **non-update** run reported the same 157/20.

**The arithmetic identifies one case, and it is the predicted one.** 21 golden-less files, 20
failed, so exactly one passed. By set difference: **`custom_predicates_n2_n1_property`** — the
case `muradin` predicted **from source alone** would be the sole pass, and pass **vacuously**
(`val x = predicate { true }` is a `FirProperty`, fails the `as? FirSimpleFunction` cast and the
`!is FirSimpleFunction` gate at `ProgramConverter:471`, so nothing converts and its empty golden
is correct).

**A static per-case prediction confirmed dynamically at individual-case granularity.** That single
result simultaneously confirms 157, confirms the golden-absence inversion, and validates
`muradin`'s analysis. **So 20 failures is the correct outcome, not a defect** — the failures are
the pass signal.

**What is not claimed, and `rune` is right to split it:** this is a gate **result**, not a passing
gate. **Verified passing gates remain ZERO.** But a flat zero now hides a real measurement, so
both halves get reported: **zero passing gates, one verified 157/20 failure that failed as
predicted.**

### The discriminating run does not exist

`sacharissa`'s point, and correct. Both runs read 157/20 and **the clean one ran FIRST** (16:56,
before the 17:15 update pass). An update pass reports the diffs it just wrote, so 20 failures is
its expected first-time behaviour. **A clean non-update run AFTER the update pass is the run that
says whether those 20 are resolved.** Cheapest high-value item left in the iteration, unactionable
under the pause. Left undone, **two identical 157/20 lines will read as "the update accomplished
nothing".**

### Gate conditions 3, 5 and 7 need rewording, not enforcing

- **7** does not discriminate a **deliberately** stale runner from an accidental one: the tree is
  116/137 by the corrected ruling's own design, and 157 executed either way.
- **3** (`BUILD SUCCESSFUL`) and **5** (0 failures) are **wrong for a golden-less tree**, where a
  correct run *must* fail.
- Admissible criterion there: **`BUILD FAILED` with exactly (golden-less count − vacuous-pass
  count) failures, all attributable to golden creation.**

## Also preserved from `sacharissa`'s final push (`acffd666`)

**`felix`'s five committed negative controls are currently INVERTED assertions** — they assert
that programs Viper **should reject** verify cleanly — because it left error markers for an update
pass that then died. **Anyone adopting `b0bf797a` adopts those five.** A merge-gate item: this is
the golden-update trap realised in committed form, and it is exactly the shape that survives a
`BUILD SUCCESSFUL`.

**`soren`: N5 closed** — the harness stops at `TestPhase.FRONTEND`, so
`FormverFunctionCalledInRuntimeException` is unreachable. And its **N2 result extends
`PREDICATE_WITHOUT_CLASS` to an unconstrained type parameter *and* to `String`**, showing the
diagnostic covers more than primitives.

**`soren`'s correction to a standing hazard note:** the pre-commit hook's "Stashing unstaged files"
uses **its own patch file under `~/.cache/pre-commit`, not `refs/stash`** — so it is **not** the
shared-ref hazard. The `git stash` ban stands on its own evidence (`zara`/`saskia`); the hook was
wrongly swept in.

## The two live safety gaps at the pause

**1. No memory monitor is armed.** `sacharissa`'s queue-drain watcher hit its exit condition at
17:35 and terminated by design; `ennio`'s expired. Host: 1087MB free, 2790MB available, three
chains up. **Neither `rune` nor this seat armed one** — both past the warn line, and arming under a
pause is starting something new. **My assessment, offered rather than acted on: the exposure is
materially lower than when the gap opened.** What a monitor protected against was *silent
destruction of uncommitted solver work*, and everything is now pushed — all six slug branches,
plus recovery refs. An OOM now would kill gradle runs, which the pause already says to let
complete or time out. **Escalated as owed, deliberately not closed.**

**2. `briar` parked at 161k believing it is finished, holding the per-case N4 rationale in context
alone.** `remo`'s extraction request either never arrived or was judged discharged — **second
sighting of the routing gap that lost `sacharissa` the pause order.** Two sightings is where it
stops being an accident. **If `briar` dies, the N4 ruling survives in the plan file with no
reasoning attached, which is worse than losing both, because nobody will know it was argued.**

Actioned by this seat, as preservation rather than new work: asked `briar` for a **verbatim dump,
no re-analysis**, pushed to `refs/pipeline/n4-rationale-briar` and confirmed with `ls-remote`,
with an explicit instruction to spend its remaining context on the dump and nothing else, and that
a rough dump pushed beats a clean one lost. It was also told its update pass rewriting others'
goldens **is not a fault of its own** — no rule then existed requiring an update pass to run on a
tree holding only its own new files.

## Succession owed

**`rune` is past 165k and needs succession if the run resumes.** Its own framing is the right one:
**the analysis in the recovery doc does not need the seat that produced it.** Everything of its is
durable at `refs/pipeline/dispatcher-recovery-rune`, `835cd7c7`, push verified remote-side, with a
new section 4a carrying the 157/20 verification, the per-case identification, the missing
discriminating run, and the condition rewording.

**This seat likewise.** See the note at the end.

## State of my three jobs at the pause

1. **Headline finding → PR #30.** Text ready and cleared on the evidence
   (`pr30-headline-section.md`). **Not pasted**, on the Launcher's instruction, until the
   operator unpauses. The `nova` verification corrected it twice, once in the sentence it turns
   on; the minute of both corrections is in that file.
2. **Step 4 → `complete/custom-predicates-step-4-iter-1.md`.** Cannot complete: the completion
   path is in the destroyed root, verified gates are zero, and the marker needs a new home. `rune`
   holds the dispatcher state and is committing it to `run-state/custom-predicates-recovery`.
3. **Step 5 dispatch.** Correctly still unfired, and now blocked on recovering the briefing from
   `remo` rather than on Step 4. Firing a Synthesizer at a dead artifact root would have it
   invent the shape of results it cannot read.
