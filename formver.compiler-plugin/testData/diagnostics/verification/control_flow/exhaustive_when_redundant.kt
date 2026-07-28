// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Choice
class A(val value: Int) : Choice
class B(val value: Int) : Choice

// The second `is A` branch is redundant: `A` and `B` are already fully covered by the first
// two branches, so FIR should classify this `when` `RedundantlyExhaustive` rather than
// `ProperlyExhaustive`. StmtConversionVisitor.kt:172 only special-cases `ProperlyExhaustive`,
// so per known-issues.md Issue 1 this is expected to reintroduce the pre-fix bug: the
// fallthrough should lower to the old fabricated `UnitLit`, producing a spurious rejection on
// an unreachable path even though `pick` is actually total.
// SHOULD-VERIFY (not should-fail): this case is expected to currently, incorrectly, fail.
@AlwaysVerify
fun <!VIPER_TEXT!>pick<!>(c: Choice): Int = when (c) {
    is A -> c.value
    is <!DUPLICATE_BRANCH_CONDITION_IN_WHEN!>A<!> -> -c.value
    is B -> c.value
}
