// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

sealed interface Expr
class Const(val value: Int) : Expr
class Neg(val operand: Const) : Expr

// The `when` is exhaustive over the sealed interface with no `else`, so the fallthrough is
// trusted unreachable. That trust must not extend to the branch bodies themselves: `Neg`'s
// branch can return a negative value, which violates the postcondition below, so verification
// must still reject this standalone should-fail case.
@AlwaysVerify
fun <!VIPER_TEXT!>evalNegated<!>(e: Expr): Int {
    val r = when (e) {
        is Const -> e.value
        is Neg -> -e.operand.value
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r >= 0<!>)
    return r
}
