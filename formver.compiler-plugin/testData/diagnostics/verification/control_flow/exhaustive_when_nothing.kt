// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A subject of static type `Nothing` makes the `when` vacuously exhaustive with zero branches and
// no `else`; FIR classifies it `ExhaustiveAsNothing` rather than `ProperlyExhaustive`. These
// probes record what type Kotlin gives such a `when`, which determines whether the missing
// `ExhaustiveAsNothing` handling can ever produce a wrong-typed fallthrough.
@AlwaysVerify
fun <!VIPER_TEXT!>fromNothingExprBody<!>(n: Nothing): Int = <!RETURN_TYPE_MISMATCH!>when (n) {
}<!>

@AlwaysVerify
fun <!VIPER_TEXT!>fromNothingIntVal<!>(n: Nothing): Int {
    val r: Int = <!INITIALIZER_TYPE_MISMATCH!>when (n) {
    }<!>
    return r
}

@AlwaysVerify
fun <!VIPER_TEXT!>fromNothingUnit<!>(n: Nothing) {
    when (n) {
    }
}
