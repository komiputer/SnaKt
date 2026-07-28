// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Shape
class A(val n: Int) : Shape
class B(val n: Int) : Shape

// A two-subtype sealed hierarchy covered by branches `is A`, `is A`, `is B`. The second `is A`
// branch is redundant, but the `when` still covers both subtypes with no `else`. The function is
// total, so it should verify as always returning an Int.
@AlwaysVerify
fun <!VIPER_TEXT!>redundantlyExhaustive<!>(s: Shape): Int = when (s) {
    is A -> s.n
    is <!DUPLICATE_BRANCH_CONDITION_IN_WHEN!>A<!> -> s.n + 1
    is B -> s.n
}
