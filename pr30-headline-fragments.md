=== fragment ===
# For PR #30's body — the headline finding

Drafted by `annie` (Planner seat, replaced `remo`) for the Launcher, who owns PR #30. This is
section text, ready to paste. Suggested position: **before** "Known limitations", since it
reframes what the feature delivers and the limitations read differently after it.

Status of the evidence at the moment of writing: every link was verified in two earlier seats
(`ennio`, `auden`) against the **main checkout at `bf32366c`**. A third independent
re-verification is in flight from this seat, in a fresh worktree via `git grep … bf32366c`,
because a claim entering public text should not rest on my reading of a predecessor's state
file. **Do not paste until that comes back** — I will send the result either way, including if
it contradicts the draft. Measured 2026-07-30T15:14Z; feature tip re-read as `458cb00f` at
15:13:52Z.

---

## What a custom predicate can and cannot do

A custom predicate can be **assumed** and **consumed**. It cannot be **established**.

Three constructs are involved and the distinction between them is what makes this precise
rather than sweeping:

| Construct | Where | Status |
|---|---|---|
| `Exp.Unfolding` — pure expression, specification contexts | `PureExpLinearizer.kt:91`, `SsaConverter.kt:117` | **Works.** Auto-inserted, no user annotation needed. This is why recursive predicate *bodies* are well-formed. |
| `Stmt.Unfold` — statement, method bodies | `Linearizer.kt:147`, `LinearizationVisitor.kt:435` | Two live sites that cannot fire. Both sit behind `unfoldToAccess`, true only for `BY_RECEIVER_UNIQUENESS`, which `Linearizer.kt:114` has already diverted to `havoc`. The wiring exists and is one predicate away from working. |
| `Stmt.Fold` | none outside `viper/ast/` | **No constructor exists anywhere in the plugin.** A `grep` returns nothing. No program ever folds a predicate. |

Predicate accesses enter a program by exactly one route, `Stmt.Inhale`: `StmtModifier.kt:32`
for permissions, and `LinearizationVisitor.kt:256`/`279` inhaling type invariants — the latter
being what this PR's `includeSubTypeInvariants()` feeds.

So the only way a program obtains `acc(P(x))` is by assuming it at a specification boundary,
and the only thing it can do with it is exhale it at a call site. **An obligation a call site
creates can never be discharged constructively, only forwarded from another assumption.**

### What follows, and how confident each part is

Verified: the three rows above, and the single `Inhale` route.

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

- `Stmt.Fold`: **zero hits** outside `viper/ast/`. The absence is **pre-existing upstream**.
- Both `Exp.Unfolding` sites **already exist** there. So the working half was not merely
  cheap — it was **inherited, not built here**.

This matters for reading the instruction this work was commissioned under. "Implement
fold/unfold automation" names two halves: **one already worked before the run started**, and
**the other has nothing to automate**, there being no constructor for it anywhere in the
plugin this builds on. That is the difference between something owed and something nobody
could have delivered under that instruction.


=== fragment ===
Status of the evidence. Every link was verified in two earlier seats (`ennio`, `auden`) against
the main checkout at `bf32366c`. This seat ran a **third independent re-verification** — worker
`nova`, fresh worktree, `git grep … bf32366c` — because a claim entering public text should not
rest on my reading of a predecessor's state file, and two seats agreeing is not three.

**It found two defects, and one of them was in the load-bearing sentence.** Both are corrected
below; the correction is minuted at the end of this file rather than silently applied, because
the difference it draws is the whole precision of the finding.

**One check is still open** — whether `PureFunBodyLinearizer`'s `accessInvariants` is
assumption-sourced. Everything below stands on the answer being yes. **Do not paste until I
confirm it**; I will send the result either way, including if it refutes the finding.
Feature tip re-read as `458cb00f` at 2026-07-30T15:13:52Z.

=== fragment ===
| `Stmt.Fold` | **nowhere** | **Not constructed anywhere in the codebase.** `git grep "Fold("` at `bf32366c` returns the `data class Fold` declaration at `Stmt.kt:207`, its own `toSilver()` body, and one `is Stmt.Fold` pattern-match at `Program.kt:166`. No construction site, inside `viper/ast/` or out. No program ever folds a predicate. |

The distinction that makes this exact: **where a predicate access is *mentioned* is not where the
permission is *acquired*.** Predicate-access expressions are constructed at four sites
(`ClassTypeEmbedding.kt:66`, `LinearizationVisitor.kt:434`, `Linearizer.kt:146`,
`PureFunBodyLinearizer.kt:111`). But the only construct that **grants** `acc(P(x))` is
`Stmt.Inhale` — `StmtModifier.kt:32` for permissions, `LinearizationVisitor.kt:256`/`279`
inhaling type invariants, the latter being what this PR's `includeSubTypeInvariants()` feeds.
Every other site **consumes** an access it must already hold: `Exp.Unfolding` requires the
permission rather than producing it, and a call site exhales.

So the only way a program obtains `acc(P(x))` is by assuming it at a specification boundary,
and the only thing it can do with it is forward it. **An obligation a call site creates can never
be discharged constructively, only passed along from another assumption.**

=== fragment ===
plugin this builds on. That is the difference between something owed and something nobody
could have delivered under that instruction.

---

## Minute of the correction — not part of the PR text

`nova`'s third verification (`git grep` at `bf32366c`, own worktree) against the four claims as
the predecessor seats stated them:

- **`Stmt.Unfold`, two guarded sites — stands verbatim.** `LinearizationVisitor.kt:435` sits
  directly inside `if (e.field.unfoldToAccess)` opening at :430; `Linearizer.kt:147` is inside
  `unfoldHierarchyPath`, called only from :128 inside `if (field.unfoldToAccess)`.
- **`Exp.Unfolding` at `PureExpLinearizer.kt:91` and `SsaConverter.kt:117` — stands, at those
  exact lines.**
- **`Stmt.Fold` — true but mis-framed.** "Zero hits outside `viper/ast/`" is literally correct and
  implies it is constructed *somewhere* inside `viper/ast/`. It is constructed **nowhere at all**.
  The weaker phrasing understates the finding; corrected above.
- **"Predicate accesses enter a program only by `Stmt.Inhale`" — FALSE as stated.** There is a
  fourth construction site, `PureFunBodyLinearizer.kt:111`, whose result flows through
  `SsaConverter.withAccessInvariants` into `Exp.Unfolding` and never touches `Stmt.Inhale`.

**Disposing of both halves of that last claim rather than dropping the remainder**, which is the
error class this run has hit repeatedly:

- The half about **construction sites** is refuted. Three named sites were not all of them.
- The half about **permission acquisition** survives, because `Exp.Unfolding` requires
  `acc(P(x))` rather than granting it — so the new site is a consumption site and its permission
  must originate elsewhere. The finding needed a sharper statement, not a retraction.

**The open check is exactly the seam that argument rests on:** where
`PureFunBodyLinearizer`'s `accessInvariants` originates. If it traces to declared preconditions
or inhaled type invariants, it is assumption-sourced and the finding holds as corrected. If any
route grants `acc(P(x))` with neither an assumption nor a `Stmt.Fold`, **the headline finding of
this run is refuted** and that goes to the Launcher unsoftened.

Worth recording why this was caught: two seats had verified the claim and agreed. The third
check was run anyway, on the ground that public text should not rest on a predecessor's state
file — and the claim it corrected was the one sentence the whole section turns on.

=== fragment ===
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

=== fragment ===
**The open check has come back and the finding holds — CLEARED TO PASTE.** `nova` traced
`PureFunBodyLinearizer`'s `accessInvariants` to its origin and found **no route by which a program
acquires `acc(P(x))` without an assumption and without `Stmt.Fold`**. But it corrected the text a
second time: the assumption channel is **not singular**, and the version above now names both.
Feature tip re-read as `458cb00f` at 2026-07-30T15:13:52Z.

