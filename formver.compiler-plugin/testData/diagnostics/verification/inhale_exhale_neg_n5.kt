// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.exhale

// Method N — N-5: impure condition argument (purity-checker rejection).
// The condition passed to inhale/exhale must be a PURE boolean expression, the
// same constraint the plugin applies to `verify`. Here we pass expressions with
// side effects (pre-/post-increment of a mutable local, which mutates state).
// This was EXPECTED to be rejected at COMPILE TIME by the purity checker (a
// PURITY_VIOLATION plugin diagnostic in the .fir.diag.txt golden), NOT reach the
// Silicon verifier. The impure-expression pattern (`x++ < ...`, `++x < ...`)
// mirrors the existing purity/assert_statements.kt test, where the plugin DOES
// flag `verify(++x < 43)` as PURITY_VIOLATION.
//
// [UNVERIFIED] ACTUAL RESULT: NO PURITY_VIOLATION is produced for inhale/exhale.
// The impure argument is silently accepted and translated (see the golden file:
// the increment is emitted as `x := plusInts(...)` and then `inhale/exhale
// intFromRef(anon) < 43`). So inhale/exhale do NOT enforce the same argument-purity
// check that `verify` enforces. This is an asymmetry / missing purity check for the
// new DSL functions. Severity: major (the documented purity constraint on the
// argument, API surface item 3, is not enforced for inhale/exhale). The golden
// file below records the ACTUAL (accepting) behavior so the test is stable.
@AlwaysVerify
fun <!VIPER_TEXT!>impureInhale<!>() {
    var x = 42
    inhale(++x < 43)
}

@AlwaysVerify
fun <!VIPER_TEXT!>impureExhale<!>() {
    var x = 42
    exhale(x++ < 43)
}
