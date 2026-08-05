// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

sealed interface Expr
class Const(val value: Int) : Expr
class Neg(val operand: Const) : Expr

// An exhaustive `when` over a sealed interface needs no `else`: `eval` returns an Int on every
// path a value of type `Expr` can take.
@AlwaysVerify
fun <!VIPER_TEXT!>eval<!>(e: Expr): Int = when (e) {
    is Const -> e.value
    is Neg -> -e.operand.value
}

// `r` is negative for `Const(-1)`, so `r >= 0` is simply false and is rejected as such.
@AlwaysVerify
fun <!VIPER_TEXT!>evalNonNeg<!>(e: Expr): Int {
    val r = when (e) {
        is Const -> e.value
        is Neg -> -e.operand.value
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r >= 0<!>)
    return r
}
