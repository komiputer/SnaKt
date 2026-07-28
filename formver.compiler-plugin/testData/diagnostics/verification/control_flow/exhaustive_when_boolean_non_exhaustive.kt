// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A `Boolean`-subject `when` with only one arm covered and no `else` is genuinely
// non-exhaustive. This exercises the untested `Boolean`-subject path (known-issues.md
// Issue 2) from the should-fail side: Kotlin's own frontend must still reject it, confirming
// the fix's trust in FIR doesn't extend to cases FIR itself calls non-exhaustive.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>describe<!>(b: Boolean): String = <!NO_ELSE_IN_WHEN!>when<!> (b) {
    true -> "yes"
}<!>
