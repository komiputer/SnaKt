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
