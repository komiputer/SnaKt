# N-2 case analysis — the 12 `custom_predicates_n2*` negative cases

Solver seat N-2 (`muradin`, replacing `briar`). Date 2026-07-30.
Tree analysed: `origin/feature/custom-predicates` tip `458cb00f`; the 12 test files land at `4ead163e`.

**This document is committed to git because the shared artifact root it originally lived in was
garbage-collected minutes after it was written, taking the strategy, the plan, the dispatcher state
and every gate artefact with it. Git is the only durable store this run has. Do not move this content
back to a scratch path.**

**This is STATIC analysis.** No gradle run backs it. Every prediction is derived from committed source
and committed goldens and is labelled by confidence. **HIGH** = follows directly from quoted
source/golden. **MEDIUM** = one untraced inference step.

---

## 0. The finding that precedes all 12 cases — no golden exists for any of them

At `4ead163e` the 12 `n2` files are **12 `.kt` and zero `.diag.txt`**. Checked independently from the
object store by the dispatcher seat across all of the new files on the feature tip: **21 `.kt`, zero
`.diag.txt`.**

`DiagnosticsCollector.assertEquality()`, in
`formver.common/src/org/jetbrains/kotlin/formver/common/services/DiagnosticsCollector.kt`:

```kotlin
fun assertEquality() {
    val testDataFile = testServices.moduleStructure.originalTestDataFiles.first()
    val expectedFile =
        testDataFile.parentFile.resolve("${testDataFile.nameWithoutExtension.removeSuffix(".fir")}${fileExtension}")

    val expectedOutput = render()
    if (expectedOutput == null && !expectedFile.exists()) return
    testServices.assertions.assertEqualsToFile(expectedFile, expectedOutput ?: "")
}
```

`render()` returns `null` iff no diagnostics were collected. So the third line from the bottom means:
**an absent golden asserts that the file produces NO diagnostics.** As committed, all 12 negative cases
assert the exact opposite of what they exist to check.

Two consequences.

1. **A non-update gate on this tree must report one FAILURE per golden-less working case.** For the 12
   `n2` files that is 12 failures if every case works as intended. **A green non-update gate here would
   mean the cases produce nothing.** The usual criterion is inverted: `0 failures` is the wrong
   criterion on this tree, and a green is the alarm.
2. **Any gate that does pass on this tree can only be an UPDATE pass**, so the golden-update trap
   applies to all 12 at once: update mode rewrites the test *source* as well as the goldens, inserting
   inline markers, so program and expectation both bend to observed behaviour. No golden from an update
   run counts without a human-legible diff, and specifically without checking whether any negative
   control was written into a golden as passing. A final non-update run must then be green with no
   golden rewritten.

### Independent dynamic corroboration

The static prediction above was confirmed by a completed run on another solver's branch, which this
seat could not observe directly: **`tests=139 failures=3 errors=0`**, where **139** is that solver's
exact predicted total (119 `runTest` + 20 `testAllFilesPresentIn*`) and **3** is exactly its number of
golden-less new cases. A static reading of `assertEquality` and an independent dynamic run agree on
both the count and the failure mechanism.

---

## 1. Mechanism, established from committed source

`ProgramConverter.embedCustomPredicate`
(`formver.compiler-plugin/core/src/org/jetbrains/kotlin/formver/core/conversion/ProgramConverter.kt`,
~lines 401-450):

- `val declaration = symbol.fir as? FirSimpleFunction ?: return null` — non-function declarations exit
  silently, reporting nothing.
- `extractPredicateDeclarationBlock() ?: run { if (declaration.mentionsPredicateBuiltin()) emit(…
  MALFORMED_PREDICATE_DECLARATION, "A \`predicate { }\` block must be the entire body of a function
  returning Boolean.") ; return null }` — **N1's detector. It fires on the DECLARATION, so an N1 case
  needs no use site.**
- `val classType = subjectType?.let { embedType(it).pretype as? ClassTypeEmbedding }`, and on `null`
  emit `PREDICATE_WITHOUT_CLASS` — **N2's detector, also declaration-level.**
- `CustomPredicateCallable.insertCall`: `if (!ctx.inSpecification) ctx.reportPredicateOutsideSpecification(
  (subject as? WithPosition)?.source, …)` — **N3's detector. It fires at the CALL SITE, so an N3 case
  does need one.**

`ProgramConverter:471` gates body conversion: `if (declaration !is FirSimpleFunction || body == null)`.

The three diagnostics all exist in `core/diagnostics/ConversionErrors.kt`:
`MALFORMED_PREDICATE_DECLARATION`, `PREDICATE_WITHOUT_CLASS`, `PREDICATE_OUTSIDE_SPECIFICATION`.

### The decisive detail for N2 — nullability is a flag, not part of the pretype

`ProgramConverter.embedTypeWithBuilder`, lines ~703-740:

```kotlin
type is ConeTypeParameterType -> {
    isNullable = true; any()
}
…
type.canBeNull(session) -> {
    isNullable = true
    embedTypeWithBuilder(type.withNullability(false, session.typeContext))
}
```

A nullable type sets a flag and **recurses into the non-null type, so the PRETYPE is unchanged**.
`Node?` therefore yields pretype `ClassTypeEmbedding(Node)` and passes the `as? ClassTypeEmbedding`
check. A type *parameter* yields `any()` and does not.

---

## 2. The `custom_predicates_unfold` golden, which rewrote the N4 reading

`testData/diagnostics/verification/classes/custom_predicates_unfold.fir.diag.txt` is committed and is
the most informative artefact on the branch. Source is a `var`-field `Interval`, an `ordered()`
predicate, and `fun readUnderPredicate(i: Interval): Int` with `preconditions { i.ordered() }` whose
body is `return i.hi`. Generated Viper, excerpted:

```
predicate ordered(v_this_extension: Ref) {
  isSubtype(typeOf(v_this_extension), Interval()) &&
  acc(Interval_unique(v_this_extension), write) &&
  intFromRef((unfolding acc(Interval_unique(v_this_extension), write) in
    v_this_extension.lo)) <=
  intFromRef((unfolding acc(Interval_unique(v_this_extension), write) in
    v_this_extension.hi))
}

method readUnderPredicate(i: Ref) returns (v_ret_0: Ref)
  requires isSubtype(typeOf(i), Interval())
  requires acc(ordered(i), write)
  ensures isSubtype(typeOf(v_ret_0), intType())
{
  v_ret_0 := havoc(intType())
  goto lbl_ret_0
  label lbl_ret_0
}
```

I expected the custom predicate to sit *alongside* the plugin's class permission predicate. It
**contains** it. Three facts follow, and they are the load-bearing part of the N4 analysis:

- **The custom predicate absorbs the class permission predicate.** The method requires
  `acc(ordered(i), write)` and **not** `acc(Interval_unique(i), write)`. Holding the custom predicate
  therefore means holding **no direct field permission at all**.
- **A field read under a held-but-unfolded custom predicate silently HAVOCS** rather than erroring:
  `return i.hi` became `v_ret_0 := havoc(intType())`.
- Combined with the headline finding — `Stmt.Fold` has no constructor reachable from the plugin
  (`Stmt.Unfold` has exactly two call sites, `Linearizer.kt:147` and `LinearizationVisitor.kt:435`,
  neither reachable from user code) — **the predicate body is never available to any caller.** A
  precondition predicate conveys nothing whatsoever about the object's fields.

---

## 3. Per case

### N1 — `MALFORMED_PREDICATE_DECLARATION`

| file | what it tests | prediction | verdict |
|---|---|---|---|
| `n1_nonboolean` | `predicate { }` as a statement inside an `Int`-returning function | `extractPredicateDeclarationBlock` → null, `mentionsPredicateBuiltin` → true, MALFORMED fires | **sound, HIGH** |
| `n1_subexpr` | `= predicate { … } && true`, block is not the whole body | same path, MALFORMED fires; needs no use site and correctly has none | **sound, HIGH** |
| `n1_property` | `val alwaysOrdered: Boolean = predicate { true }` | **NO diagnostic.** A `FirProperty` fails the `as? FirSimpleFunction` cast at the head of `embedCustomPredicate` and the `!is FirSimpleFunction` gate at `ProgramConverter:471`. The initializer is never converted; nothing is reported | **VACUOUS, HIGH** |

`n1_property` is the only one of the 12 whose **committed empty golden is correct**, and therefore the
only one that would pass a non-update gate — while testing nothing. It passes because the misuse is
invisible to the plugin, not because it is handled. **This is a coverage finding, not unsoundness:** a
property-bound `predicate` never enters a specification, so nothing false is assumed. The plan's fifth
N1 bullet is not implementable through the function-symbol path at all, and should be recorded as an
acknowledged gap rather than kept as a green.

### N2 — `PREDICATE_WITHOUT_CLASS`

| file | what it tests | prediction | verdict |
|---|---|---|---|
| `n2_typeparam` | `fun <T> T.generic(): Boolean = predicate { true }` | `ConeTypeParameterType` → `any()`; pretype is not a `ClassTypeEmbedding`; PREDICATE_WITHOUT_CLASS fires **on the declaration**, so the `x.generic()` use site is not load-bearing | **sound, HIGH** |
| `n2_nullable` | `fun Node?.maybeOrdered(): Boolean = predicate { true }`, used in `preconditions` | **PREDICATE_WITHOUT_CLASS does NOT fire.** `canBeNull` strips nullability and recurses, so the pretype *is* `ClassTypeEmbedding(Node)`. The predicate is **accepted**, declared on `Node`, with the receiver's nullability silently dropped | **not a negative case at all. HIGH on the mechanism, MEDIUM on the downstream behaviour** |

`n2_nullable` is the plan's own designated flagging case — expected behaviour unstated in the API
document, record what happens, flag it if it is neither a clean diagnostic nor a clean acceptance. The
answer is **silent acceptance with nullability dropped.** `useMaybeOrdered(n: Node?)` will require
`acc(maybeOrdered(n))` for a possibly-null `n`, while the predicate body opens with
`isSubtype(typeOf(v), Node())`, which does not hold for `null`. The precondition is therefore
**unsatisfiable when `n` is null** — assumable, never establishable. That is the headline finding
reached by a second, independent route.

The case needs a `.fir.diag.txt` recording **accepted Viper text**, not a diagnostic marker.
**If an update pass writes an empty golden here and it reads as a passing negative control, that is
the golden-update trap firing.**

### N3 — `PREDICATE_OUTSIDE_SPECIFICATION`

All four hinge on `insertCall` observing `inSpecification == false`.

| file | what it tests | prediction | verdict |
|---|---|---|---|
| `n3_ifcond` | `if (i.ordered())` in an ordinary body | fires | **sound, HIGH** |
| `n3_argument` | `accept(i.ordered())` | fires | **sound, HIGH** |
| `n3_lambda` | `val check = { i.ordered() }; check()` | fires — the lambda body **is** converted, inlined at the invocation site; see the trace below | **sound, HIGH** |
| `n3_enclosing` | `Chain.checkOutsideBlock()` calls the sibling predicate `descending()` | fires, but see below | **fires, HIGH; MISALIGNED with the bullet it was written for** |

#### `n3_lambda` — traced, and it closes at HIGH

The chain is complete and every link is in committed source:

1. `val check = { i.ordered() }` → `visitProperty` → `data.declareLocalProperty(symbol,
   property.initializer?.let { data.convert(it) })`. Converting the initializer hits
   `visitAnonymousFunctionExpression`, which returns `LambdaExp(signature, function, data, label)` and
   **does not convert the body**. So far the worry held.
2. `check()` → `visitImplicitInvokeCall`. The receiver is a `FirPropertyAccessExpression`, and
   `data.embedLocalSymbol(receiverSymbol).ignoringMetaNodes()` matches `is LambdaExp ->`, calling
   `exp.insertCall(args, data, returnType)`. **The local-`val` hop resolves.**
3. `LambdaExp.insertCall` **inlines the body**: `ctx.insertInlineFunctionCall(signature, …, inlineBody,
   labelName, parentCtx)`. The body is therefore converted, and `i.ordered()` inside it reaches
   `CustomPredicateCallable.insertCall`.
4. `inSpecification` is a **dynamic counter on the `ProgramConverter`**, not a per-context or lexical
   property:

   ```kotlin
   private var specificationDepth: Int = 0
   override val inSpecification: Boolean get() = specificationDepth > 0
   override fun <R> withinSpecification(action: () -> R): R {
       specificationDepth++
       try { return action() } finally { specificationDepth-- }
   }
   ```

   So it does not matter that `insertCall` carries `parentCtx` from the lambda's *creation* site: at
   `check()` in an ordinary body the depth is 0 either way, and `PREDICATE_OUTSIDE_SPECIFICATION` fires.

**`n3_lambda` is sound, HIGH.** That moves the tally to **7 sound of 12** and leaves no open case.

Step 4 also exposes a coverage gap that no current case probes: because the flag is a **dynamic** depth
counter rather than a lexical check, the diagnostic depends on where a predicate call is **invoked**,
not where it is written. A lambda written in ordinary code but invoked *inside* a specification block
would **not** be reported, and one written inside a specification but invoked outside it **would**.
Neither shape is covered by these 12 cases. Recording it as a gap, not a defect — dynamic scoping is a
defensible choice here, and nothing establishes it is wrong.

Two notes on `n3_enclosing`.

- The plan's fifth N3 bullet is "called from another predicate's **enclosing function** but outside its
  `predicate { }` block". What is written is a **sibling method of the same class**, which is
  behaviourally the already-covered "called from an ordinary function body" case. **It duplicates
  covered ground rather than discriminating the commissioned shape.**
- `insertCall` reports on `(subject as? WithPosition)?.source`. For an implicit dispatch receiver the
  subject may not be a `WithPosition`, giving a **source-less** diagnostic. That still renders —
  `DiagnosticsCollector` handles `KtDiagnosticWithoutSource` via `listOf(it.firstRange)` — but it
  positions differently from its three siblings. Expect its golden to look unlike theirs and **do not
  read that as a defect.**

### N4 — genuine Viper rejection. This is where the class breaks down.

| file | intended ground | predicted actual ground | verdict |
|---|---|---|---|
| `n4_missing_precondition_at_callsite` | call site does not establish the callee's predicate precondition | insufficient permission to `acc(ordered(i))` at the call to `needsOrdered` — **names the predicate** | **sound, HIGH** |
| `n4_weak_precondition` | precondition too weak to establish the postcondition | postcondition `it > 0` fails because `lo >= 0` is **never learnable**, not because it is too weak | **fails for the WRONG reason, HIGH** |
| `n4_mutated_object` | predicate falsified by mutation, then reused | **permission failure on the write**, or silent havoc and a **vacuous pass** | **cannot test its stated property. HIGH on the cause, MEDIUM on which outcome** |

**`n4_missing_precondition_at_callsite` is the only unambiguously satisfied N4 case.** Its rejection is
a permission message *about the predicate itself*, so here the permission ground and the predicate
ground **coincide** rather than compete — the one configuration in which the plan's warning does not
bite. Corollary worth stating: since no user code can `fold`, this case **cannot be made to pass**,
which is a coverage statement about the positive suite rather than a defect in the negative one.

**`n4_weak_precondition` is a false negative control.** `Interval(val lo, val hi)`, precondition
`i.nonNegativeLo()` (`lo >= 0`), postcondition `it > 0`, body `return i.lo`. It fails — but under the
folded `nonNegativeLo(i)` the caller has no access to the body, so `lo >= 0` is not in scope at all.
**The postcondition would fail identically if the precondition were `lo >= 1`, i.e. if the predicate
were strong enough.** The diagnostic ("postcondition might not hold") cannot distinguish "too weak"
from "body invisible". Per the Planner's rev-5 ruling this is **a reportable finding, not a failed
case.** Turning it into a real test requires a companion case with a *sufficient* predicate that also
fails, which would demonstrate the missing discriminator rather than assume it.

**`n4_mutated_object` — the highest-value case in the class, and the analysis says it cannot do its
job.** `Interval(var lo, var hi)`; `preconditions { i.ordered() }`; then `i.hi = i.lo - 1`; then
`needsOrdered(i)`. Per §2 the field permission sits inside `acc(ordered(i))` and the caller holds no
direct permission, so the write has no permission behind it. Either:

- **(a)** Viper rejects the write for **insufficient permission to `i.hi`** — a rejection on a
  *permission* ground where a *predicate-falsification* ground was expected. A textbook false negative
  control, and it would read as success. Or
- **(b)** the write is havoc'd or dropped the way `readUnderPredicate`'s read was, `acc(ordered(i))` is
  still held at the call because **mutating a field does not consume a predicate access**, and
  `needsOrdered(i)` **VERIFIES**.

Outcome (b) is the one to watch, with two qualifications that must stay attached wherever this is
quoted:

- **It is NOT the standing stop-and-escalate trigger.** That trigger is a control which **omits** the
  predicate and still verifies. This case **supplies** it.
- **It is NOT unsoundness.** `preconditions { i.ordered() }` is an *assumption*, and since no user code
  can ever establish `ordered`, the assumption is unsatisfiable in practice and verifying under it is
  **vacuous — which is Viper behaving exactly as designed.**

What it does establish is that **the property class N4 was created to cover — Viper attending to a
predicate that has been falsified — is not reachable from this plugin at all while `fold` is
unconstructible.**

---

## 4. Summary

| verdict | count | cases |
|---|---|---|
| Sound — fails for the stated reason | **7** | `n1_nonboolean`, `n1_subexpr`, `n2_typeparam`, `n3_ifcond`, `n3_argument`, `n3_lambda`, `n4_missing_precondition_at_callsite` |
| Fires, but tests other than commissioned | 1 | `n3_enclosing` — duplicates covered ground |
| Vacuous — misuse invisible to the plugin | 1 | `n1_property` |
| Not a negative case; silent acceptance to be recorded | 1 | `n2_nullable` |
| **False negative control — fails on the wrong ground** | **2** | `n4_weak_precondition`, `n4_mutated_object` |

The two N4 findings and `n2_nullable` reduce to the same root cause as the headline finding: with no
user-reachable `fold`, and with the class permission predicate nested inside the custom predicate, **a
custom predicate's body is never available to any caller.** Every attempt to write a negative case
about predicate *content* collapses into a permission or a havoc observation. **That is one finding
with three witnesses, not three findings.**
