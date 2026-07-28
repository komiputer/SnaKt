// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

// An exhaustive `when` over a Boolean subject with no `else` is total: the missing
// fallthrough is unreachable, so the function verifies as always returning an Int.
// The commit message names this path explicitly but it was never tested.
@AlwaysVerify
fun <!VIPER_TEXT!>boolValue<!>(b: Boolean): Int = when (b) {
    true -> 1
    false -> 0
}

// Trusting exhaustiveness must not weaken branch-body checking.
@AlwaysVerify
fun <!VIPER_TEXT!>boolValuePositive<!>(b: Boolean): Int {
    val r = when (b) {
        true -> 1
        false -> 0
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r > 0<!>)
    return r
}
