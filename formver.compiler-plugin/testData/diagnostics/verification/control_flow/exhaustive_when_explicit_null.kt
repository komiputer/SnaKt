// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Opt
class Present(val v: Int) : Opt
class Absent(val v: Int) : Opt

// A nullable sealed subject whose `null` case is an explicit branch. Both `when`s below are
// genuinely exhaustive with no syntactic `else`, so the missing fallthrough is unreachable and the
// nullability gate must not send them to the imprecise `UnitLit` fallback.
@AlwaysVerify
fun <!VIPER_TEXT!>explicitNull<!>(x: Opt?): Int = when (x) {
    null -> 0
    is Present -> x.v
    is Absent -> x.v
}

@AlwaysVerify
fun <!VIPER_TEXT!>explicitNullSubjectVal<!>(x: Opt?): Int = when (val y = x) {
    null -> 0
    is Present -> y.v
    is Absent -> y.v
}
