// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

<!INTERNAL_ERROR, INTERNAL_ERROR, INTERNAL_ERROR, VIPER_VERIFICATION_ERROR!><!VIPER_TEXT!>enum class Sign<!> { POS, NEG, ZERO }<!>

// An exhaustive `when` over an enum subject with no `else` is total: the missing
// fallthrough is unreachable, so the function verifies as always returning an Int.
@AlwaysVerify
fun signValue(s: Sign): Int = when (s) {
    Sign.POS -> 1
    Sign.NEG -> -1
    Sign.ZERO -> 0
}

// Trusting exhaustiveness must not weaken branch-body checking: the assertion below
// is violated by the ZERO branch, so this must still fail to verify.
@AlwaysVerify
fun signValueNonZero(s: Sign): Int {
    val r = when (s) {
        Sign.POS -> 1
        Sign.NEG -> -1
        Sign.ZERO -> 0
    }
    verify(r != 0)
    return r
}
