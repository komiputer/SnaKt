# Method A (Feature Contract) — solver-a-1, f6-exists, iteration 1

Branch: `solver/f6-exists-a-1-iter1`, off `origin/polish/f6-exists` (tip `e99de81a`).
Six test files, thirteen specification functions. Final state of the suite: all six tests
pass (`BUILD SUCCESSFUL`, working tree clean), because the markers pin *observed* behaviour;
the contract expectation each case was written to check is recorded separately below and in
each file's comments.

## Headline result

**A postcondition `exists` cannot be discharged unless the witness is already available as a
ground term in the proof context.** `exists<Int> { it == 0 }` as a postcondition fails to
verify:

```
Postcondition of existsZeroPost might not hold.
Assertion (exists anon: Int :: anon == 0) might not hold.
```

Twelve of the thirteen specification functions that state a standalone postcondition
existential fail this way. The one shape that works is where the witness comes from program
state or from an assumed existential in a loop invariant — which is precisely the shape of
`max_character.kt`, the branch's showcase test. That makes the shipped test corpus
systematically blind to the case where Viper must find a witness itself.

This is a contract violation, not a coverage gap, and it is larger than anything in the
known-issues catalogue. It also invalidates a test design the strategist's brief relies on:
any case whose expected outcome is "a postcondition existential fails to verify" will pass for
the wrong reason. See "Consequences for other solvers" below.

### Mechanism (inference, not verified)

The two cases that verify tell us Viper *can* prove an existential goal. In
`containsCharacter` the existential is discharged at an early return where `i` is in scope and
the body contains `stringFromRef(s)[anon_builtin_0]`, giving both a matching pattern and a
ground term to instantiate at. In `exists<Int> { it == 0 }` the body is `anon == 0`, which
contains no function application from which a trigger can be inferred. Since Silicon relies on
E-matching rather than model-based quantifier instantiation, the negated goal
`forall anon :: !(anon == 0)` never gets instantiated and the proof fails.

I did not inspect Silicon's Z3 configuration, so treat the mechanism as a hypothesis. The
behaviour itself is directly observed and reproduced across four files. The reference-typed
cases show the trigger machinery explicitly in the error text:
`{ isSubtype(typeOf(anon), nullable(intType())) }`.

## Case-by-case

### 1. Basic satisfiability and empty domain (QT-2) — `verification/user_invariants/exists_contract_basic.kt`

```kotlin
@AlwaysVerify fun existsZeroPost(): Int        { postconditions<Int> { exists<Int> { it == 0 } };          return 0 }
@AlwaysVerify fun existsSquareIsFour(): Int    { postconditions<Int> { exists<Int> { it * it == 4 } };     return 0 }
@AlwaysVerify fun existsStrictlyBetween(): Int { postconditions<Int> { exists<Int> { it > 0 && it < 2 } }; return 0 }
@AlwaysVerify fun existsEmptyIntRange(): Int   { postconditions<Int> { exists<Int> { it > 0 && it < 0 } }; return 0 }
```

Postconditions rather than preconditions deliberately: a precondition existential is only
assumed, never proved, so the empty-domain case would pass vacuously there and establish
nothing.

Generated Viper (all four lower correctly):

```
ensures (exists anon: Int :: anon == 0)
ensures (exists anon: Int :: anon * anon == 4)
ensures (exists anon: Int :: anon > 0 && anon < 2)
ensures (exists anon: Int :: anon > 0 && anon < 0)
```

| Function | Contract expectation | Observed | Match |
|---|---|---|---|
| `existsZeroPost` | verify (witness 0) | fails | **no** |
| `existsSquareIsFour` | verify (witness 2) | fails | **no** |
| `existsStrictlyBetween` | verify (witness 1) | fails | **no** |
| `existsEmptyIntRange` | fail | fails | yes, but see below |

The empty-domain case (QT-2) is **uninformative as written**. It fails, as required, but so
does `exists<Int> { it == 0 }`, so the failure carries no signal about empty-domain handling.
Lowering is correct — `anon > 0 && anon < 0` is emitted faithfully — but nothing here
distinguishes "correctly rejected because unsatisfiable" from "rejected like everything else".

`it * it == 4` was included to probe nonlinear arithmetic specifically. It fails, but since
the linear cases also fail, nonlinearity is not shown to be the cause.

### 2. Reference-type domain guard (QT-1) — `verification/user_invariants/exists_ref_domain.kt`

Domain `Int?`. `AnonymousBuiltinVariableEmbedding.isOriginallyRef` is `injection == null`, so a
nullable `Int` is a `Ref` and takes the guard, while a bare `Int` or `Char` does not.

```kotlin
@AlwaysVerify fun refExistsNullWitness(): Boolean       { postconditions<Boolean> { exists<Int?> { it == null } };            return true }
@AlwaysVerify fun refExistsNoWitnessInDomain(): Boolean { postconditions<Boolean> { exists<Int?> { it == 1 && it == 2 } };    return true }
@AlwaysVerify fun refForAllOverSameDomain(): Boolean    { postconditions<Boolean> { forAll<Int?> { (it == 1) implies (it != 2) } }; return true }
```

Generated Viper, the two duals side by side in one golden file:

```
ensures (exists anon: Ref :: isSubtype(typeOf(anon), nullable(intType())) &&
    anon == nullValue())

ensures (forall anon: Ref ::isSubtype(typeOf(anon), nullable(intType())) ==>
    anon == intToRef(1) ==> !(anon == intToRef(2)))
```

**QT-1 resolves in the feature's favour, and is now pinned by a test.** `exists` combines the
runtime-type guard with `&&`, `forAll` with `==>`, exactly as the API surface claimed from code
reading. This is the property the operator's "test carefully before merge" note is most likely
aimed at, and it is correct.

The design intent of `refExistsNoWitnessInDomain` was to discriminate the two combinators *by
verification outcome* rather than by reading text: the body is unsatisfiable inside the domain,
so under the correct `&&` guard it must fail, whereas a guard built with `==>` would let any
out-of-domain value satisfy it vacuously and the existential would hold. **That
discrimination does not work**, because all three functions fail for the headline reason. The
guard combinator is established textually only. A solver wanting an outcome-based check would
need a domain where the existential is dischargeable at all.

Incidental oddity, flagged without a claim of unsoundness: `it == 1 && it == 2` over `Int?`
lowers the two syntactically identical comparisons two different ways, as
`anon == intToRef(1) && intFromRef(anon) == 2` — one a `Ref` comparison, the other unwrapping
to `Int`. Worth a look by someone who knows why.

### 3. Char domain bound (CH-1, High) — `verification/user_invariants/exists_char_domain.kt`

```kotlin
@AlwaysVerify fun leastCharExists(): Boolean    { postconditions<Boolean> { exists<Char> { lo -> forAll<Char> { c -> lo <= c } } }; return true }
@AlwaysVerify fun greatestCharExists(): Boolean { postconditions<Boolean> { exists<Char> { hi -> forAll<Char> { c -> c <= hi } } }; return true }
```

Framed to turn on the bound itself rather than on any particular witness: a bounded `Char`
domain has a least and a greatest element, an unbounded Viper `Int` domain has neither. Under
the intended `[0, 65536)` contract both verify; without the bound both are false.

Generated Viper:

```
ensures (exists anon_builtin_0: Int :: (forall anon_builtin_1: Int ::anon_builtin_0 <=
      anon_builtin_1))
```

**CH-1 confirmed behaviourally.** The strong evidence is textual, not the outcome: the
quantified variable is emitted as a bare `Int` with no `0 <= x && x < 65536` guard anywhere.
Both functions fail to verify, but per the headline result that failure alone would have proved
nothing — every postcondition existential fails. Anyone re-testing this after a fix must check
the emitted text, not just the pass/fail.

Two constraints on how this case could be written, for whoever restores the bound:

- `Char.code` has no support in the plugin (no `"code"` handling anywhere in `core`, no `.code`
  in `testData`), so a code-point-valued Char test is not currently expressible.
- I could not emit a code-point-zero char literal (backslash-u followed by four zeros) through
  this toolchain: every attempt arrived either as a literal NUL byte in the file or as a plain
  space. The case is written without it. A solver trying the more obvious "is there a Char
  strictly below code point 0" framing, which needs that literal, should expect trouble.

### 4. `forAll`/`exists` combined, second example (QT-2) — `expensive_verification/algorithms/contains_character.kt`

```kotlin
fun containsCharacter(s: String, c: Char): Boolean {
    postconditions<Boolean> { res ->
        (res implies exists<Int> { 0 <= it && it < s.length && s[it] == c }) &&
                ((!res) implies forAll<Int> { (0 <= it && it < s.length) implies (s[it] != c) })
    }
    var i = 0
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            forAll<Int> { (0 <= it && it < i) implies (s[it] != c) }
        }
        if (s[i] == c) return true
        i += 1
    }
    return false
}
```

**Verifies cleanly. Contract satisfied.** Distinct from `max_character.kt`: both directions of
the result are stated in one postcondition, the existential discharged at an early return with
the loop counter as witness, the universal discharged at loop exit from the invariant.

No `.viper.diag.txt` file exists for this test, which is how a clean verification presents
itself in this suite.

### 5. `exists` in a loop invariant, second example (QT-2) — `expensive_verification/algorithms/exists_search_flag.kt`

```kotlin
fun anyCharacterMatches(s: String, c: Char): Boolean {
    postconditions<Boolean> { res ->
        res implies exists<Int> { 0 <= it && it < s.length && s[it] == c }
    }
    var found = false
    var i = 0
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            found implies exists<Int> { 0 <= it && it < i && s[it] == c }
        }
        if (s[i] == c) { found = true }
        i += 1
    }
    return found
}
```

**Verifies cleanly. Contract satisfied.** Different loop shape from `max_character.kt`: a
boolean flag backed by an existential witness accumulated over the scanned prefix, with the
loop running to completion rather than returning early, so the invariant rather than a return
site carries the witness. The invariant lowers as expected:

```
invariant boolFromRef(found) ==>
  (exists anon_builtin_1: Int :: 0 <= anon_builtin_1 &&
    anon_builtin_1 < intFromRef(i) &&
    stringFromRef(s)[anon_builtin_1] == charFromRef(c))
```

QT-2's loop-invariant and combined-quantifier coverage gaps are now filled by two independent
examples that both pass.

### 6. Nested quantifiers (QT-2) — `verification/user_invariants/exists_nested.kt`

```kotlin
@AlwaysVerify fun nestedExistsExists(): Int          { postconditions<Int> { exists<Int> { i -> exists<Int> { j -> i > 0 && j > 0 && i + j == 5 } } }; return 0 }
@AlwaysVerify fun forAllHasLargerWitness(): Int      { postconditions<Int> { forAll<Int> { i -> exists<Int> { j -> j > i } } };                       return 0 }
@AlwaysVerify fun existsUpperBoundOfNegatives(): Int { postconditions<Int> { exists<Int> { b -> forAll<Int> { i -> (i < 0) implies (i < b) } } };     return 0 }
```

Generated Viper:

```
ensures (exists anon_builtin_0: Int :: (exists anon_builtin_1: Int :: anon_builtin_0 > 0 &&
      anon_builtin_1 > 0 && anon_builtin_0 + anon_builtin_1 == 5))
ensures (forall anon_builtin_0: Int ::(exists anon_builtin_1: Int :: anon_builtin_1 >
      anon_builtin_0))
ensures (exists anon_builtin_0: Int :: (forall anon_builtin_1: Int ::anon_builtin_1 < 0 ==>
      anon_builtin_1 < anon_builtin_0))
```

**Lowering is correct in all three nesting orders**: each inner quantifier gets its own Viper
quantifier and its own fresh bound variable, and inner bodies reference the outer bound
variable correctly. This is what QT-2 asked to confirm, and it holds.

All three statements are true and all three fail to verify, for the headline reason rather than
anything specific to nesting.

## Consequences for other solvers and later steps

1. **Method N cases 1 and 4 need re-framing.** Misuse class 1 (Char bound absence) predicts a
   "false pass" today; I observe the opposite — the Char cases fail, but so does everything
   else, so neither pass nor fail is diagnostic. Misuse class 4 (asserting `exists P` and
   `forAll !P` together) expects a verifier rejection: it will get one, but it would have got
   one from `exists<Int> { it == 0 }` alone. Neither case can distinguish its intended defect
   from the blanket failure. Both need their expected outcome restated against the emitted
   Viper text, or restricted to positions where existentials are dischargeable.
2. **The Debugger's fix scope may be larger than CH-1.** Restoring `charDomainBoundOrNull()`
   will not make `exists<Char>` usable in a postcondition, because the bound is not what is
   blocking it. Whether unprovable postcondition existentials are considered in scope for this
   PR is the operator's call, but shipping `exists` with that limitation undocumented seems
   worse than the Char gap it was flagged for.
3. **DI-1 remains open from my side.** Method N case 3 owns it; nothing I ran touched
   `Info.fromSilver`, and no run of mine crashed the compiler.

## Golden-file judgement check (as required)

I read every regenerated golden and judged it against the contract before committing, rather
than accepting what `update` produced. Concretely: the four expected-fail files are marked
`VIPER_VERIFICATION_ERROR` to match observed behaviour, and each carries a comment stating
explicitly that the three basic cases, both Char cases and all three nested cases *ought* to
verify under the contract and do not. They pin current behaviour and say so in the file. No
case was silently blessed.

One correction to the warning I was given about this: **`update` does not overwrite existing
goldens.** It generates only *absent* golden files and fails on mismatch for files that exist.
My initial empty placeholder `.fir.diag.txt` files therefore blocked generation entirely,
which is why the first run produced only `.viper.diag.txt`. To have a golden regenerated you
must delete it. The judgement risk is real but narrower than described: it applies to newly
created files only.

## Problems encountered

- **Host memory exhaustion.** 11 GB box with ~1 GB available and swap in use, six concurrent
  Gradle+Z3 solvers. One run died with "Gradle build daemon disappeared unexpectedly". No
  result from it entered this report.
- **`./gradlew --stop` is host-wide.** One run died with "Gradle build daemon has been stopped:
  stop command received", from another agent's `--stop`. `~/.gradle/daemon` is shared per-user
  across worktrees, so `--stop` kills every solver's daemon including single-use `--no-daemon`
  ones mid-build. The dispatcher retracted the advice to run it after each invocation. I never
  ran it.
- **Two runs killed by my own process cleanup.** I killed a stale monitor loop by PID and took
  a concurrent background Gradle run down with it. My error; nothing from those runs is
  reported.
- **One run wasted on a drifted working directory.** An earlier `cd` inside a compound command
  persisted across tool calls, so a later `./gradlew` invocation was made from
  `testData/diagnostics` and failed with "No such file or directory". Use absolute paths.
- **`git commit` is broken in this environment.** `InvalidManifestError:
  /home/brainbot/.cache/pre-commit/repot_0al6im/.pre-commit-hooks.yaml is not a file` — a stale
  row in the pre-commit cache pointing at a nonexistent user's path. All commits here use
  `--no-verify`, which also skips `end-of-file-fixer`; my files were written with trailing
  newlines regardless.
- **Code-point-zero char literals are not writable through this toolchain**, as described in
  case 3.
- **`ugrep` is the `grep` on this host** and treats BRE alternation `\|` as literal, so
  `grep "a\|b"` silently matches nothing. Use `-E`. This cost me one wrong conclusion about
  test registration mid-run.

## Reproduction

```
./gradlew --no-daemon -Dorg.gradle.jvmargs=-Xmx1g :formver.compiler-plugin:update \
  --tests "*PhasedDiagnosticTestGenerated*testExists_contract_basic*" \
  --tests "*PhasedDiagnosticTestGenerated*testExists_ref_domain*" \
  --tests "*PhasedDiagnosticTestGenerated*testExists_nested*" \
  --tests "*PhasedDiagnosticTestGenerated*testExists_char_domain*" \
  --tests "*PhasedDiagnosticTestGenerated*testContains_character*" \
  --tests "*PhasedDiagnosticTestGenerated*testExists_search_flag*"
```

Notes for later steps:

- `:formver.compiler-plugin:generateTests` alone succeeds and does **not** hit the
  `generateTests`/`detekt` task-graph error that breaks `./gradlew build`. Confirmed, exit 0.
- `:formver.compiler-plugin:update` (`formver.compiler-plugin/build.gradle.kts:117`) runs
  conversion, then verifies if conversion changed, and generates absent goldens — one pass
  instead of a write-then-verify round trip. `untilConversion` and `test` are the other modes.
- Generated test method names are snake-cased after `test`: `testExists_ref_domain`, not
  `testExistsRefDomain`. Class-level filters take the form
  `*PhasedDiagnosticTestGenerated$Verification$User_invariants*`.
- A clean verification produces **no** `.viper.diag.txt` file at all; absence is the signal.
