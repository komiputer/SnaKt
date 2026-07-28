// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// FIR reports `ExhaustiveAsNothing` only for a `when` with a `Nothing` subject and *zero* branches.
// Such a `when` has type `Unit`, so Kotlin rejects it in an `Int`-returning position before the
// plugin's choice of fallthrough value can matter.
@AlwaysVerify
fun <!VIPER_TEXT!>fromNothing<!>(n: Nothing): Int = <!RETURN_TYPE_MISMATCH!>when (n) {
}<!>

// Adding a branch gives the `when` a non-`Unit` type, but it is then no longer branch-empty, so the
// status is `ProperlyExhaustive` rather than `ExhaustiveAsNothing` and the fallthrough is marked
// unreachable as usual.
@AlwaysVerify
fun <!VIPER_TEXT!>fromNothingBranch<!>(n: Nothing): Int = when (n) {
    <!USELESS_IS_CHECK!>is Number<!> -> 1
}

sealed interface Empty

// A sealed interface with no subtypes is not `Nothing`, so FIR treats a branchless `when` over it
// as non-exhaustive rather than `ExhaustiveAsNothing`.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>fromEmpty<!>(e: Empty): Int = <!NO_ELSE_IN_WHEN!>when<!> (e) {
}<!>
