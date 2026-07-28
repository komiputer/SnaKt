# Repro: KT-4999219 (stale exhaustiveness across separate compilation)

Not runnable through the standard golden-test harness (`test-gen`), because it
requires two separate compiler invocations against the *same* sealed
interface at two different subtype counts, with the `when`'s enclosing
function compiled only against the older count. The single-file diagnostic
harness compiles one file per test in one pass, so it cannot express "compile
A, then swap A for a binary-incompatible A′ without recompiling B".

## Steps (plain `kotlinc`/K2JVMCompiler 2.3.0, no SnaKt plugin attached)

```sh
kotlinc -d libv1out Expr.v1.kt              # sealed interface with A, B
kotlinc -d appout -cp libv1out Main.kt      # when(e){is A->...; is B->...}, no else
kotlinc -d libv2out Expr.v2.kt              # same interface, now with A, B, C
kotlinc -d runnerout -cp "libv2out:appout" Runner.kt   # println(test(C))
java -cp "libv2out:appout:runnerout" RunnerKt
```

## Observed result (Kotlin 2.3.0, SnaKt's pinned version)

`Main.kt`'s `test` compiles cleanly against `libv1out` (FIR reports the `when`
`ProperlyExhaustive` — correctly, relative to the two subtypes visible at that
compile). `Runner.kt` compiles cleanly against `libv2out` + the *stale*
`appout` classes. Running it throws:

```
Exception in thread "main" kotlin.NoWhenBranchMatchedException
	at MainKt.test(Main.kt:1)
```

## Assessment

Confirmed reproducible at the plain-Kotlin/bytecode level: a function whose
`when` FIR judged `ProperlyExhaustive` at its own compile time throws at
runtime once a downstream module extends the sealed hierarchy and the
consumer isn't recompiled.

This is **not** a FIR mis-classification within a single compile — FIR's
verdict at `Main.kt`'s compile time is correct for what it could see then.
It's the general binary-compatibility hazard of sealed hierarchies across
separately-compiled modules (the reason build systems must force full
recompilation of dependents on a sealed hierarchy's ABI change). SnaKt adds
no runtime check of its own either way — `@AlwaysVerify`'s proof is a
static, compile-time claim, so it is exposed to exactly this same staleness
window as the exhaustiveness check itself, no better and no worse. A SnaKt
consumer who trusts `@AlwaysVerify`'s totality proof across a library
upgrade without re-verifying is in the same position as any Kotlin caller
relying on `NO_ELSE_IN_WHEN` across the same upgrade.

Flagging per the testing plan's instruction (reproducible ⇒ escalate), but
recommending it be tracked as a **documentation caveat** ("SnaKt's
verification results assume the current classpath's sealed-hierarchy ABI;
re-verify after any dependency upgrade that touches a verified function's
sealed types") rather than a code defect in this commit — the commit
introduces no new exposure relative to plain Kotlin's existing behavior.
