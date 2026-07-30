# N4 per-case rationale (solver-n-2 / Briar) — dumped verbatim from context, no re-checking against source

Three N4 cases, all under `formver.compiler-plugin/testData/diagnostics/verification/classes/`:
`custom_predicates_n2_n4_weak_precondition.kt`,
`custom_predicates_n2_n4_missing_precondition_at_callsite.kt`,
`custom_predicates_n2_n4_mutated_object.kt`.

Background fact I was relying on (from the API surface doc, §"Compiler/plugin behaviour" and the
later reconciliation broadcast about fold/unfold): custom predicates in this feature are only ever
**inhaled** (assumed) at a specification boundary and **exhaled** at a call site. There is no `fold`
statement anywhere in the codebase (`Stmt.Fold` is constructed nowhere outside `viper/ast/`), so a
predicate instance can never be constructively *established* — only assumed via a precondition and
then forwarded/consumed. This was later confirmed independently by another seat (`auden`/`ennio`'s
finding, broadcast into the API surface doc) after I had already designed these cases on the same
reasoning. I did not re-derive my rationale against that broadcast; I record both below since they
converge but I have not cross-checked them line by line.

## Case 1 — `custom_predicates_n2_n4_weak_precondition.kt`

```kotlin
class Interval(val lo: Int, val hi: Int)

fun Interval.nonNegativeLo(): Boolean = predicate { lo >= 0 }

fun weakPrecondition(i: Interval): Int {
    preconditions { i.nonNegativeLo() }
    postconditions<Int> { it > 0 }
    return i.lo
}
```

**Why I judged this a genuine verifier rejection, not a prohibition trip:**
- `lo` is declared `val`, not `var`. This matters because the known plugin-wide limitation
  (`BY_RECEIVER_UNIQUENESS` havoc) only fires for `var` field reads in a method body; a `val`
  property embeds as a permission-free Viper *function* (per the API doc: "no permission is
  required"). So reading `i.lo` inside `weakPrecondition`'s body does not trip the havoc prohibition
  — it should read as a well-defined (if unconstrained) value.
- Because the custom predicate `nonNegativeLo` is never unfolded at the client (`weakPrecondition`),
  the verifier has no access to the predicate's *content* (`lo >= 0`) even though `weakPrecondition`
  holds `acc(nonNegativeLo(i))`. Holding an opaque predicate instance in separation logic does not
  expose what's inside it without an explicit unfold, and this plugin never emits one.
- So the postcondition `it > 0` must be proved from nothing but `i.lo`'s bare type constraint — which
  is insufficient regardless of what the predicate says. This should fail.
- **Uncertain / not verified further:** whether the failure Silicon reports names this as a
  postcondition violation (what I intended to test) versus some other message shape. I designed the
  case to fail "for the right reason" (insufficient information to establish the postcondition, not a
  havoc or missing-field-permission artifact) but I did not get to read the resulting
  `.viper.diag.txt` / inline markers before losing context. **This needs to be checked by whoever
  picks this up**, specifically: confirm the rejection is a postcondition-assertion failure, not a
  `MALFORMED_PREDICATE_DECLARATION`-style diagnostic or an unrelated compile error.

## Case 2 — `custom_predicates_n2_n4_missing_precondition_at_callsite.kt`

```kotlin
class Interval(val lo: Int, val hi: Int)

fun Interval.ordered(): Boolean = predicate { lo <= hi }

fun needsOrdered(i: Interval) {
    preconditions { i.ordered() }
}

fun callWithoutEstablishing(i: Interval) {
    needsOrdered(i)
}
```

**Why I judged this a genuine verifier rejection:**
- `callWithoutEstablishing` does not itself hold `i.ordered()` (no precondition, no prior
  establishment). Calling `needsOrdered(i)` requires exhaling `acc(ordered(i))` at the call site.
- Given the "assumed and consumed, never established" property (no `fold` exists anywhere in the
  plugin), there is no way for `callWithoutEstablishing` to legitimately come to hold
  `acc(ordered(i))` other than receiving it as an assumption of its own — which it does not do here.
- So the call should fail to discharge the callee's precondition — a clean, textbook
  precondition-not-established rejection, and (I believe) the *most* textbook / safest of the three
  N4 cases, least likely to trip an unrelated prohibition, since it uses only `val` fields and no
  mutation at all.
- **Uncertain:** same caveat as case 1 — I did not confirm the actual Silicon message before losing
  context. I expect "Method call might fail. There might be insufficient permission to access
  `ordered(i)`" or equivalent Viper phrasing, but this is inference from how Viper normally reports
  precondition-not-established at a call site, not something I read off this case's own golden.

## Case 3 — `custom_predicates_n2_n4_mutated_object.kt`

```kotlin
class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate { lo <= hi }

fun needsOrdered(i: Interval) {
    preconditions { i.ordered() }
}

fun mutateThenCall(i: Interval) {
    preconditions { i.ordered() }
    i.hi = i.lo - 1
    needsOrdered(i)
}
```

**This is the case I was least confident about, and flagged as such at design time.**

- Here `lo`/`hi` are `var` (deliberately, to express "mutation"), unlike cases 1–2. `mutateThenCall`
  establishes `i.ordered()` via its own precondition (an assumption, fine), then writes `i.hi`, then
  calls `needsOrdered(i)` which again requires `i.ordered()`.
- My concern at design time: writing `i.hi` requires *write permission* to that field. Since the
  predicate access `acc(ordered(i))` is (per the "never folded" property) an opaque resource that
  does not expose the field permissions bundled inside it (`C$unique`'s `acc(v_this.hi, write)` etc.)
  without an unfold — and no unfold is ever emitted — I reasoned the write `i.hi = ...` might itself
  be rejected for **missing field permission**, before the program ever gets to the second call to
  `needsOrdered`. That would still be "a genuine Viper rejection" in the sense the brief asked for
  (not one of our own diagnostics, not the havoc prohibition), but it would **not** be testing what
  the misuse class intends ("an object mutated so its predicate no longer holds, then passed where
  the predicate is required") — it would instead be demonstrating a *different*, more basic
  limitation: that you cannot mutate a field at all once its containing predicate has been folded
  into an opaque access, regardless of whether the mutation would break the predicate's content or
  not.
- I explicitly flagged this ambiguity as something to resolve by reading the actual rejection
  message once goldens were generated — i.e., check whether the failure is at the `i.hi = ...`
  statement (permission error on the write) or at the second `needsOrdered(i)` call (permission
  error on re-establishing `ordered(i)`). **I did not get to do this check before losing context.**
  Whoever picks this up should read `custom_predicates_n2_n4_mutated_object.viper.diag.txt` (if a
  golden was generated by my update-flag run — I believe one was, since my golden-update pass
  reported writing `.fir.diag.txt`/`.viper.diag.txt` files for my n4 cases, but I have not confirmed
  contents) and identify which statement is flagged, and whether the message text refers to
  permission on the field write or on the predicate re-access.
- Given the later broadcast ("a custom predicate can be assumed and consumed, but never
  established" / "an obligation a call site creates can never be discharged constructively, only
  forwarded from another assumption"), I now think it's *plausible* the case fails at the
  `needsOrdered(i)` call regardless of the mutation's effect, simply because nothing in
  `mutateThenCall` re-establishes `ordered(i)` as fresh — the original assumption from its own
  precondition might already have been "spent"/exhaled by the intervening write, or might still be
  intact and get re-exhaled successfully (in which case the mutation would be silently ignored by the
  verifier, and the program would **wrongly verify** — the soundness-finding scenario the brief
  explicitly asked to watch for). I could not resolve this without reading the actual result.
  **This is the case most likely to contain either a soundness finding (wrongly verifies) or a
  wrong-reason failure (fails on the write, not the mutation's semantic effect) — it deserves the
  closest look of the three.**

## Addendum — goldens observed just before pushing this

Immediately before writing this out, the actual current file contents (with inline diagnostic
markers from the golden-update pass) surfaced in my context. Reporting them since they directly
resolve the open uncertainty above; I did not go looking for them, and did no further analysis
beyond reading the markers:

- `weak_precondition`: whole function wrapped in `<!VIPER_VERIFICATION_ERROR!>`. Confirms Case 1
  fails as a genuine Viper verification error (not one of our own diagnostics).
- `missing_precondition_at_callsite`: only the call `<!VIPER_VERIFICATION_ERROR!>needsOrdered(i)<!>`
  is marked, at the call site inside `callWithoutEstablishing`. Confirms Case 2 fails exactly where
  predicted — precondition not established at the call, not somewhere else.
- `mutated_object`: **no `VIPER_VERIFICATION_ERROR` marker anywhere in the file at all** — neither on
  the `i.hi = i.lo - 1` write nor on the `needsOrdered(i)` call. The function `mutateThenCall` is
  marked only with `<!VIPER_TEXT!>`, the same bare marker `needsOrdered` itself gets (i.e. "renders as
  Viper", not "fails verification"). Read plainly, this means the program **verifies successfully**
  even though it mutates `i.hi` after establishing `ordered(i)` and then re-passes `i` to a function
  requiring `ordered(i)` again — i.e. **this is the soundness-finding branch of my three-way split
  above, not a wrong-reason-failure or a clean rejection.** This should be escalated, not quietly
  folded into "genuine N4 rejection" — as written, this test case does NOT belong in the N4
  (genuine verifier rejection) bucket at all; it currently demonstrates a should-fail program that
  passes. I have not re-run the gate myself to confirm this marker reflects a real Silicon pass
  rather than a stale/partial golden-update artifact — that confirmation is still owed.

## Summary table (as I understood it at design time, unconfirmed against actual gate output)

| Case | Intended failure point | Confidence it fails for the *intended* reason | Status |
|---|---|---|---|
| weak_precondition | postcondition assertion, insufficient info from unfolded predicate | Medium-high | Not confirmed against golden |
| missing_precondition_at_callsite | call-site precondition not established | High | Not confirmed against golden |
| mutated_object | ambiguous: could fail on the field write itself, could fail on re-establishing the predicate, or could wrongly verify (soundness finding) | Low — flagged as needing manual golden inspection | Not confirmed against golden — **highest priority to check** |
