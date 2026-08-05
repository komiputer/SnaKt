// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

sealed interface Val
class IntVal(val n: Int) : Val
class BoolVal(val b: Boolean) : Val

// The fallthrough of a `when` that is *not* exhaustive is an ordinary reachable path. If it were
// treated as impossible, `x` would be 1 on every path and the assertion would go through.
@AlwaysVerify
fun <!VIPER_TEXT!>reachableFallthroughStaysReachable<!>(a: Boolean) {
    var x = 0
    when {
        a -> x = 1
    }
    verify(<!VIPER_VERIFICATION_ERROR!>x == 1<!>)
}

// An empty `when` is all fallthrough. Nothing after it may be treated as dead code.
@AlwaysVerify
fun <!VIPER_TEXT!>emptyWhenIsNotAnAssumption<!>() {
    when { }
    verify(<!VIPER_VERIFICATION_ERROR!>false<!>)
}

// The impossibility of the fallthrough is confined to the fallthrough. It must not reach the join
// point after the `when`, where both real branches arrive.
@AlwaysVerify
fun <!VIPER_TEXT!>impossibilityDoesNotEscapeTheBranch<!>(v: Val): Int {
    val r = when (v) {
        is IntVal -> v.n
        is BoolVal -> 0
    }
    verify(<!VIPER_VERIFICATION_ERROR!>false<!>)
    return r
}

// Trusting the compiler's totality proof says nothing about what the branches compute. A false
// claim about the result must still be rejected.
@AlwaysVerify
fun <!VIPER_TEXT!>branchBodiesStillChecked<!>(v: Val): Int {
    val r = when (v) {
        is IntVal -> v.n
        is BoolVal -> 0
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r > 0<!>)
    return r
}

// Nor may the totality of one `when` be used to conclude anything about the subject itself.
@AlwaysVerify
fun <!VIPER_TEXT!>totalityIsNotASubjectFact<!>(v: Val): Int {
    val r = when (v) {
        is IntVal -> v.n
        is BoolVal -> 0
    }
    verify(<!VIPER_VERIFICATION_ERROR!>v is IntVal<!>)
    return r
}
