// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.exhale

// Method N — N-1: cold exhale.
// `exhale(x > 0)` with NO prior inhale, no precondition, no other proof basis.
// `x` is unconstrained, so `x > 0` is not provable. This must be REJECTED by the
// Silicon verifier (a verification error at the exhale), NOT a compile-time error:
// the program is well-typed and pure, so it must reach the verifier and fail there.
@AlwaysVerify
fun <!VIPER_TEXT!>coldExhale<!>(x: Int) {
    exhale(<!VIPER_VERIFICATION_ERROR!>x > 0<!>)
}
