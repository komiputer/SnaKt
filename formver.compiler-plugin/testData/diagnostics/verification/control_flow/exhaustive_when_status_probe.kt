// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Val
class VInt(val v: Int) : Val
class VStr(val v: Int) : Val

// A complete sealed cover followed by a redundant `else`. If FIR classifies this
// `RedundantlyExhaustive`, the syntactic `else` still supplies the fallthrough, so the
// exhaustiveness status cannot affect lowering.
@AlwaysVerify
fun <!VIPER_TEXT!>redundantElse<!>(x: Val): Int = when (x) {
    is VInt -> x.v
    is VStr -> x.v
    <!REDUNDANT_ELSE_IN_WHEN!>else<!> -> 0
}

// A `Nothing`-typed subject with a branch, so the `when`'s type comes from the branch rather than
// defaulting to `Unit`. This is the only shape in which `ExhaustiveAsNothing` could produce a
// wrong-typed fallthrough.
@AlwaysVerify
fun <!VIPER_TEXT!>nothingWithBranch<!>(n: Nothing): Int = when (n) {
    <!USELESS_IS_CHECK!>is Val<!> -> 1
}

// A `Boolean` subject covered by `true` alone plus the redundant `false` and a further duplicate.
@AlwaysVerify
fun <!VIPER_TEXT!>boolDuplicated<!>(b: Boolean): Int = when (b) {
    true -> 1
    <!DUPLICATE_BRANCH_CONDITION_IN_WHEN!>true<!> -> 2
    false -> 0
}
