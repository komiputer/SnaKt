// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.exhale
import org.jetbrains.kotlin.formver.plugin.verify

// `inhale(x > 0)` assumes the fact without proving it; the subsequent `verify` is then provable
// only because of the inhale.
@AlwaysVerify
fun <!VIPER_TEXT!>inhaleThenRely<!>(x: Int) {
    inhale(x > 0)
    verify(x > 0)
    verify(x >= 1)
}

// Establish a fact, then `exhale` it. The exhale asserts the fact and transfers it out of the
// proof state.
@AlwaysVerify
fun <!VIPER_TEXT!>establishThenExhale<!>(x: Int) {
    inhale(x > 5)
    exhale(x > 0)
}
