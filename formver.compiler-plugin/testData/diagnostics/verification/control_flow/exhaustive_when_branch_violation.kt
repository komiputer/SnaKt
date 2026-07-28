// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

sealed interface Val
class Pos(val n: Int) : Val
class Wrapped(val inner: Pos) : Val

// Trusting the `when`'s totality must not suppress checking of the branch bodies. `n` is an
// arbitrary Int, so the assertion inside the `is Pos` branch is violable.
@AlwaysVerify
fun <!VIPER_TEXT!>checkInBranch<!>(v: Val): Int = when (v) {
    is Pos -> {
        verify(<!VIPER_VERIFICATION_ERROR!>v.n > 0<!>)
        v.n
    }
    is Wrapped -> v.inner.n
}

// The same at the use site of the `when`'s result: the `is Wrapped` branch can produce a negative
// value, so the assertion fails despite the `when` being total.
@AlwaysVerify
fun <!VIPER_TEXT!>magnitude<!>(v: Val): Int {
    val r = when (v) {
        is Pos -> v.n
        is Wrapped -> -v.inner.n
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r >= 0<!>)
    return r
}
