# For PR #30's body — the headline finding

Drafted by `annie` (Planner seat, replaced `remo`) for the Launcher `niamh`, who owns PR #30.
Section text, ready to paste. Suggested position: **before** "Known limitations", since it
reframes what the feature delivers and the limitations read differently after it.

**CLEARED TO PASTE.** Evidence status: verified in two earlier seats (`ennio`, `auden`) against
the main checkout at `bf32366c`, then **independently re-verified by worker `nova`** in a fresh
worktree via `git grep … bf32366c`. That third check corrected the text twice — once because the
`Stmt.Fold` claim *understated* the finding, once because the load-bearing sentence about
`Stmt.Inhale` was false. Both corrections are in the text below and minuted at the end.

Feature tip re-read as `458cb00f` at 2026-07-30T15:13:52Z.

This copy exists because the artifact root was emptied at ~15:20:40Z, taking
`artifacts-history` with it. Recovered from this seat's own transcript on disk, not from memory.

---

## What a custom predicate can and cannot do

A custom predicate can be **assumed** and **consumed**. It cannot be **established**.

Three constructs are involved and the distinction between them is what makes this precise
rather than sweeping:

| Construct | Where | Status |
|---|---|---|
| `Exp.Unfolding` — pure expression, specification contexts | `PureExpLinearizer.kt:91`, `SsaConverter.kt:117` | **Works.** Auto-inserted, no user annotation needed. This is why recursive predicate *bodies* are well-formed. |
| `Stmt.Unfold` — statement, method bodies | `Linearizer.kt:147`, `LinearizationVisitor.kt:435` | Two live sites that cannot fire. Both sit behind `unfoldToAccess`, true only for `BY_RECEIVER_UNIQUENESS`, which `Linearizer.kt:114` has already diverted to `havoc`. The wiring exists and is one predicate away from working. |
| `Stmt.Fold` | **nowhere** | **Not constructed anywhere in the codebase.** `git grep "Fold("` at `bf32366c` returns the `data class Fold` declaration at `Stmt.kt:207`, its own `toSilver()` body, and one `is Stmt.Fold` pattern-match at `Program.kt:166`. No construction site, inside `viper/ast/` or out. No program ever folds a predicate. |

The distinction that makes this exact: **where a predicate access is *mentioned* is not where the
permission is *acquired*.** Predicate-access expressions are constructed at four sites
(`ClassTypeEmbedding.kt:66`, `LinearizationVisitor.kt:434`, `Linearizer.kt:146`,
`PureFunBodyLinearizer.kt:111`). Most of those **consume** an access that must already be held:
`Exp.Unfolding` requires the permission rather than producing it, and a call site exhales.

Permission is **acquired** by two structurally distinct channels, and both are assumptions:

- **`Stmt.Inhale`, for method bodies.** `StmtModifier.kt:32` for permissions, and
  `LinearizationVisitor.kt:256`/`279` inhaling type invariants — the latter being what this PR's
  `includeSubTypeInvariants()` feeds.
- **The Viper `Function.pres` precondition list, for pure functions.** Via
  `ContractBuilder.userFunctionPreconditions()` → `ContractBuilder.kt:123` →
  `FunctionSignature.kt:154`. A Viper `Function` has an expression body, not a statement
  sequence, so `Stmt.Inhale` cannot appear in one at all; a function's body may assume its own
  precondition, and that is what `PureFunBodyLinearizer.kt:111`'s `Exp.Unfolding` draws on.

Both channels assume. **Neither establishes.** So the only way a program obtains `acc(P(x))` is
by assuming it at a specification boundary, and the only thing it can do with it is forward it.
**An obligation a call site creates can never be discharged constructively, only passed along
from another assumption.**

### What follows, and how confident each part is

Verified: the three rows above, the four construction sites, and the two acquisition channels.

Stated as **inference**, not proof:

- The feature cannot express that a constructor or factory produces an object satisfying its
  predicate. There is no program shape that establishes a predicate for a concrete object.
- **This is not unsoundness.** Preconditions are legitimately assumptions in Viper. But a
  custom predicate constrains nothing about any object's actual state, so **a positive test
  case built on one risks passing vacuously** — it may demonstrate only that an assumption
  propagates.
- With no `fold`, no program can ever be required to show that a predicate's body matches
  reality. So the negative controls in this run discriminate predicate-access **presence**, not
  **content**: a caller omitting the precondition fails to supply `acc(P(x))` whatever `P`'s
  body says. The presence hole closes; the body-content hole has no available discriminator.

### This is a property of the plugin, not a gap this change left

Checked against `bf32366c`, the exact commit this branch was cut from, which carries none of
this work:

- `Stmt.Fold`: **no construction site anywhere.** The absence is **pre-existing upstream**.
- Both `Exp.Unfolding` sites **already exist** there. So the working half was not merely
  cheap — it was **inherited, not built here**.

This matters for reading the instruction this work was commissioned under. "Implement
fold/unfold automation" names two halves: **one already worked before the run started**, and
**the other has nothing to automate**, there being no constructor for it anywhere in the
plugin this builds on. That is the difference between something owed and something nobody
could have delivered under that instruction.

---

# Second, separate PR #30 item — the plugin crashes on a predicate in a subexpression

**A distinct section, not part of the one above.** Launcher ruling: this is a finding about the
feature and belongs in PR #30, and it **must not be folded into the 20 failures** of `briar`'s
update pass. Held unpasted with the rest while the run is paused.

`testCustom_predicates_n2_n1_subexpr` fails with a **`FileAnalysisException` caused by
`kotlin.NotImplementedError`** — an unimplemented `TODO` in the plugin reached during analysis.

That is **not a diagnostic and not a verification failure. It is a crash**, and it is the first
crash this run has surfaced. The distinction is the point: a diagnostic is the plugin
successfully declining a program, whereas this is the plugin failing to analyse one. A user
writing a predicate call in a subexpression position gets an internal compiler error rather than
a message about their code.

Found by `briar`'s N1 subexpression case, which is the class of case that exists to probe
where the embedding stops rather than to demonstrate the feature working.

---

## Minute of the corrections — not part of the PR text

`nova` (sonnet, own worktree, `git grep` at `bf32366c`) checked the four claims as the
predecessor seats stated them. Two stood, two did not.

**Stood verbatim.** `Stmt.Unfold`'s two guarded sites: `LinearizationVisitor.kt:435` sits
directly inside `if (e.field.unfoldToAccess)` opening at :430; `Linearizer.kt:147` is inside
`unfoldHierarchyPath`, called only from :128 inside `if (field.unfoldToAccess)`. And both
`Exp.Unfolding` sites, at the exact lines recorded.

**`Stmt.Fold` was mis-framed in a way that *understated* the finding.** "Zero hits outside
`viper/ast/`" is literally true and implies it is constructed somewhere *inside* `viper/ast/`.
It is constructed nowhere at all. A hedge inherited from the shape of the original grep and
carried through three revisions.

**"Predicate accesses enter a program only by `Stmt.Inhale`" was FALSE**, and this was the
sentence the section turns on. A fourth construction site exists —
`PureFunBodyLinearizer.kt:111` → `SsaConverter.withAccessInvariants` → `Exp.Unfolding` — which
never touches `Stmt.Inhale`.

**Both halves disposed of, per the over-retraction rule.** The *construction-site* half is
refuted: three named sites were not all of them. The *permission-acquisition* half survives,
because `Exp.Unfolding` requires `acc(P(x))` rather than granting it, so the fourth site
consumes an access originating elsewhere. **The finding needed a sharper statement, not a
retraction.**

**Then my own correction was itself incomplete, and `nova` caught that too.** I had rewritten
the claim as "the only construct that grants `acc(P(x))` is `Stmt.Inhale`". Tracing
`accessInvariants` to its origin showed the assumption channel is **not singular**: pure
functions acquire the permission through the Viper `Function.pres` list, not through any
statement, because a Viper `Function` has no statement sequence to inhale in. So the second
draft was still falsifiable on its mechanism even though its substance held. The text above
names both channels.

**Answer to the load-bearing question: no route was found by which a program acquires
`acc(P(x))` without an assumption and without `Stmt.Fold`.** `nova` was briefed in exactly
those terms, told that such a route would refute the run's headline finding and that it should
say so plainly rather than soften it. Two assumption channels, zero establishment channels.

Worth recording why any of this was caught. Two seats had verified the claim and agreed — but
they had a **common source**, the same grep restated, and agreement between readings of one
measurement is not independent confirmation. The third check cost one sonnet turn and corrected
the section's central sentence twice.
