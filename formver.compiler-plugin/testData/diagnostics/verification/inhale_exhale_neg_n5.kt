// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.exhale

// Method N — N-5: impure condition argument (purity-checker rejection).
// The condition passed to inhale/exhale must be a PURE boolean expression, the
// same constraint the plugin applies to `verify`. Here we pass expressions with
// side effects (pre-/post-increment of a mutable local, which mutates state).
// Impure arguments are rejected at compile time with a PURITY_VIOLATION diagnostic,
// matching the behavior of `verify(++x < 43)` in purity/assert_statements.kt.
@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>impureInhale<!>() {
    var x = 42
    inhale(<!PURITY_VIOLATION!>++x < 43<!>)
}

@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>impureExhale<!>() {
    var x = 42
    exhale(<!PURITY_VIOLATION!>x++ < 43<!>)
}
