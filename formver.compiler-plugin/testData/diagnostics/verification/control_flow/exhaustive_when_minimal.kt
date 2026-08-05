// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Signal
class Go(val value: Int) : Signal

// The smallest program in which the missing fallthrough matters: one implementation, one branch,
// no `else`. `pass` must return an Int on every path, and the only path without a branch body is
// the one the sealed hierarchy makes impossible.
@AlwaysVerify
fun <!VIPER_TEXT!>pass<!>(s: Signal): Int = when (s) {
    is Go -> s.value
}
