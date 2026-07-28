// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// Adversarial (Issue 1): the subject's static type is Nothing, so FIR is expected to
// classify this zero-branch `when` as `ExhaustiveAsNothing` rather than
// `ProperlyExhaustive`. The current code only treats `ProperlyExhaustive` as
// fallthrough-unreachable, so this is expected to spuriously fail to verify.
@AlwaysVerify
fun <!VIPER_TEXT!>fromNothing<!>(n: Nothing): Int = <!RETURN_TYPE_MISMATCH!>when (n) {
}<!>
