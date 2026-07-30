5. Ask the Launcher for a snapshot after amending this file.

## Revision 6 — `annie`, Planner seat, replaced `remo`

Amended as I go. Seated 2026-07-30T15:12Z. Everything below is verified in this seat unless marked.

### Measurements taken in this seat, each with its moment

- `planner-state.md` at spawn: 909 lines / `74d67762ac82f1a69fbf8df8ea31938e`, 15:12:34Z — matched the
  Launcher's spawn-time figure. Re-measured after the Launcher's retraction message: **929 lines /
  `b80a04eb7f9500aad4c8937d389a7015`, 15:15:21Z** — matched hers. The spawn-time version contained the
  retracted 136 claim; the 929-line one carries the retraction.
- `origin/feature/custom-predicates` tip: **`458cb00f`** ("Revert accidental runner regeneration
  commits"), read at **15:13:52Z** after a `git fetch`.
- `custom-predicates-step-4-dispatcher-state-iter-1-rev2.md`: 675 lines / `1995b719…` at 15:13:07Z, then
  **686 lines / `a4c9137f2970f66034ee708e90dac12c` at 15:15:49Z**. It moved under me inside three
  minutes; `rune` is amending it live. **Any figure quoted from that file needs its moment attached.**

### The 136→157 retraction did not propagate to the file `rune` reads — the defect class again

`remo` retracted "a FULL gate on the current tree correctly reports 136" and fixed it in **this** file
(lines ~818–832). At 15:15:49Z the **dispatcher** state file rev2 still asserted the retracted claim at
~line 100: *"while the branch sits at 116/137 a FULL gate on it reports 136, not 157."* That is the
document `rune` actually reads, and `rune` was seated after the retraction was made.

So the **"fix landing where the error was noticed, not where it propagated"** class fired **on a
retraction of that same class**, one section below where it is documented. This seat sent `rune` the
correction directly at 15:16Z rather than editing another seat's state file. **A retraction is not
propagated until the documents your subordinates read carry it — check those, not the one you were
writing when you noticed.**

The substance, held as the Launcher and `remo` corrected it: **the expected feature-tree gate figure is
157.** `compileTestKotlin dependsOn(generateTests)` and `generateTests` declares
`inputs.dir(testData)`, so regeneration always precedes compile and the committed 116 cannot govern what
runs. **The hazard is reading a vacuous 136 as success** — 136 on this tree means the 21 new files were
not wired in and none of the new cases ran. Slug targets: `barr` 139, `soren` 145, `felix` 142.

**The generalisable rule, and it is the best thing to come out of the `remo` seat: when two readings of a
number differ, prefer the one that fails loudly.** A false alarm costs a lock slot; a false green costs
the deliverable.

### Name the tree, not just the command

`rune`'s correction to `remo`, adopted here: the runner invariant is a **committed-tree** check only when
read off the object store. `barr`'s 119/119 and `soren`'s 125/125 were true of **post-build working
trees** and false of their commits — committed `runTest` is 116 on all three slug branches. Both readings
are consistent because they measure different trees. **"The pair holds" without naming the tree is the
measurement-provenance defect again.** So a count now needs three things: the command, the extraction
method, and the tree.

### Headline finding — routed to PR #30, not left in a state file

Drafted as paste-ready section text at **`handoffs/pr30-headline-section.md`** and handed to the Launcher,
who owns the PR. Checked against PR #30's current body first: it already carries the `!!` KDoc defect, the
runner merge gate, the ungated-commits section and the gates section, and it **does not** carry the
headline finding. Suggested position is before "Known limitations", since it reframes them.

**Deliberately not pasted yet.** A third independent re-verification is in flight — a sonnet worker
spawned at 15:14Z into its own fresh `SnaKt` worktree, checking all four links with
`git grep … bf32366c` rather than against the main checkout, so no guard question arises. Grounds: a
claim entering **public** text should not rest on this seat's reading of a predecessor's state file, and
two seats agreeing is not three. The draft states its own evidence status inline and says not to paste
until that returns. If it contradicts the draft, that result goes to the Launcher unchanged.

### Step 5 — briefing located, not reconstructed

`remo`'s briefing is `.step5-synthesizer-briefing.md` in `remo`'s own tree, which this seat cannot read.
`remo` was **still AWAKE mid-turn at 15:15Z**, so rather than wait for the tree to become unreachable and
then reconstruct the provenance sentence from memory — which the Launcher explicitly forbade, correctly,
since that sentence *is* the artefact — this seat asked `remo` to copy it into
`handoffs/step5-synthesizer-briefing.md` and report line count and md5 in the same action as the reply.
**Retrieve a dying seat's artefacts while it is still alive, not when you need them.**

Dispatch stays held until `complete/custom-predicates-step-4-iter-1.md` exists. A Synthesizer with no
results would invent their shape.

### Parked on, at rev 6 open

- `rune` — the count correction, and Step 4's completion marker.
- `remo` — the Step 5 briefing copy plus its checksum.
- the sonnet verification worker — the headline finding's third check.


### The third verification found two defects, one in the load-bearing sentence

`nova` (sonnet, own worktree, `git grep` at `bf32366c`) reported at ~15:18Z. **Two of the four
claims the headline finding rests on were wrong, and the seats that verified them agreed with each
other.** Full minute in `handoffs/pr30-headline-section.md`; the substance:

- `Stmt.Unfold`'s two guarded sites and both `Exp.Unfolding` sites **stand verbatim**, at the exact
  lines recorded.
- **`Stmt.Fold` was mis-framed in a way that *understated* the finding.** "Zero hits outside
  `viper/ast/`" is literally true and implies it is constructed somewhere *inside* `viper/ast/`. It
  is constructed **nowhere at all** — only the `data class` declaration, its own `toSilver()`, and
  one `is Stmt.Fold` match at `Program.kt:166`. A hedge inherited from the shape of the original
  grep, carried through three revisions.
- **"Predicate accesses enter a program only by `Stmt.Inhale`" is FALSE as stated.** A fourth
  construction site exists — `PureFunBodyLinearizer.kt:111` → `SsaConverter.withAccessInvariants`
  → `Exp.Unfolding`, never touching `Stmt.Inhale`.

**Both halves disposed of, per the over-retraction rule.** The *construction-site* half is refuted:
three named sites were not all of them. The *permission-acquisition* half survives, because
`Exp.Unfolding` **requires** `acc(P(x))` rather than granting it, so the fourth site consumes an
access that must originate elsewhere. **The finding needed a sharper statement, not a retraction** —
and the sharper statement is better than what it replaced: *where a predicate access is mentioned is
not where the permission is acquired.* Four sites mention; one grants.

**One check open, and it is the seam the surviving half rests on:** where
`PureFunBodyLinearizer`'s `accessInvariants` originates. `nova` re-driven at 15:19Z. If it traces to
declared preconditions or inhaled type invariants it is assumption-sourced and the finding holds as
corrected; if any route grants `acc(P(x))` with neither an assumption nor a `Stmt.Fold`, **the
headline finding is refuted** and goes to the Launcher unsoftened. `nova` was told exactly that, in
those terms, so it has no incentive to confirm.

**The transferable part: a third check on a claim two seats already agreed on is not redundant.**
This run's discipline is entirely about provenance, and the two agreeing seats had a *common source*
— the same grep, restated. Agreement between readings of one measurement is not independent
confirmation. The check cost one sonnet turn and corrected the sentence the section turns on.

### Step 5 — briefing located, not reconstructed

