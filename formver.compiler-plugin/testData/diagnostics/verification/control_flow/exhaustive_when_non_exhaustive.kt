// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Expr
class Const(val value: Int) : Expr
class Neg(val operand: Const) : Expr
class Add(val left: Expr, val right: Expr) : Expr

// `Add` is not covered by any branch and there is no `else`, so this `when` is genuinely
// non-exhaustive. Kotlin's own frontend must reject this before SnaKt's lowering runs:
// the fix that trusts `ProperlyExhaustive` must not suppress this diagnostic.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>eval<!>(e: Expr): Int = <!NO_ELSE_IN_WHEN!>when<!> (e) {
    is Const -> e.value
    is Neg -> -e.operand.value
}<!>
