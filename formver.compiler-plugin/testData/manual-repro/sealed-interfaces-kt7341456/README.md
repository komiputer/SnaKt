# Repro: KT-7341456 (false-negative NON_EXHAUSTIVE_WHEN via Java platform type)

Not runnable through the standard golden-test harness: that harness compiles
only `.kt` files, and this repro needs a Java source (`JavaSource.java`)
compiled separately so its return type is seen by Kotlin as an unannotated
platform type (`Foo!`, nullable-or-not unknown), not a Kotlin-native type.

## Steps (plain `kotlinc`/K2JVMCompiler 2.3.0, no SnaKt plugin attached)

```sh
kotlinc -d sealedout Sealed.kt                       # sealed interface Foo { Bar, Baz }
javac -cp sealedout -d javaout JavaSource.java        # JavaSource.get(): Foo (platform type, can be null)
kotlinc -d appout -cp "sealedout:javaout" Main.kt      # when(f){is Bar->; is Baz->}, no else
java -cp "sealedout:javaout:appout" MainKt
```

`Main.kt`'s `when` subject `f` is `JavaSource.get(flag)` — a platform type.
`JavaSource.get` returns `null` when `flag` is `false`.

## Observed result (Kotlin 2.3.0, SnaKt's pinned version)

Compiles with **no exhaustiveness diagnostic** (no `NO_ELSE_IN_WHEN`, no
warning) even though the `when` covers only `Bar`/`Baz` and the platform
type can be `null`. Running with `flag = false`:

```
Exception in thread "main" kotlin.NoWhenBranchMatchedException
	at MainKt.test(Main.kt:3)
```

## Assessment: CONFIRMED — direct soundness escalation for SnaKt

Unlike KT-4999219, this happens **within a single compile unit** — no
separate-compilation trick needed. FIR itself classifies this `when` as
exhaustive (otherwise `kotlinc` would have rejected it, as it does for the
genuinely-non-exhaustive case in Method N's case 1). Combined with the
code inspection already on record in
`surface/sealed-interfaces-known-issues.md` Issue 1
(`StmtConversionVisitor.kt:172` trusts `exhaustivenessStatus` unconditionally,
with no independent null/platform-type check), this means: a SnaKt function
of this shape, annotated `@AlwaysVerify` with a non-`Unit` return type, would
have its missing-`null`-branch fallthrough lowered to `inhale false` and
would **verify as total**, while it demonstrably throws
`NoWhenBranchMatchedException` at runtime for the exact input exercised
above. This is a real unsound-accept, not merely a missed opportunity to
verify.

Escalate as a new **High**-priority defect: SnaKt's trust model has no
defense against sealed subjects arriving through Java-interop platform
types, and this is reproducible today against the pinned Kotlin 2.3.0
frontend. Recommended mitigation (not applied here, out of solver scope):
reject trusting `exhaustivenessStatus` when the `when` subject's type is a
platform type (flexible type) that Kotlin hasn't proven non-null, or require
an explicit null-safety check before trusting FIR's classification for such
subjects.
