// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

sealed class Expr2
class Const2(val value: Int) : Expr2()
class Neg2(val operand: Const2) : Expr2()

// Same as exhaustive_when.kt, but the hierarchy is a sealed class instead of a sealed
// interface. The lowering path is identical, so this should also verify.
@AlwaysVerify
fun <!VIPER_TEXT!>eval2<!>(e: Expr2): Int = when (e) {
    is Const2 -> e.value
    is Neg2 -> -e.operand.value
}

@AlwaysVerify
fun <!VIPER_TEXT!>eval2NonNeg<!>(e: Expr2): Int {
    val r = when (e) {
        is Const2 -> e.value
        is Neg2 -> -e.operand.value
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r >= 0<!>)
    return r
}
