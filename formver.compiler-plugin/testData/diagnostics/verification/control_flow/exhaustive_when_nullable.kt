// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Opt
class Present(val v: Int) : Opt
class Absent(val v: Int) : Opt

// A nullable sealed subject narrowed to non-null by a prior `if (x == null) ... else`, then
// covered exhaustively over the non-null cases with no `else` (the shape from KT-7301055). If FIR
// does not mark the inner `when` exhaustive, SnaKt falls back to the old `UnitLit` fallthrough,
// which is safe but imprecise.
@AlwaysVerify
fun <!VIPER_TEXT!>narrowed<!>(x: Opt?): Int {
    if (x == null) {
        return 0
    } else {
        return when (x) {
            is Present -> x.v
            is Absent -> x.v
        }
    }
}

// Same narrowing expressed with a `when` subject variable.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>narrowedSubjectVal<!>(x: Opt?): Int {
    if (x == null) return 0
    return <!NO_ELSE_IN_WHEN!>when<!> (val y = x) {
        is Present -> y.v
        is Absent -> y.v
    }
}<!>
