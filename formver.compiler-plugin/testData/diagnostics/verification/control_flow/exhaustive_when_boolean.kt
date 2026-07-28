// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

// A `when` over a `Boolean` subject covering both `true` and `false` is exhaustive without an
// `else`, so the function is total and verifies as always returning an Int.
@AlwaysVerify
fun <!VIPER_TEXT!>toInt<!>(b: Boolean): Int = when (b) {
    true -> 1
    false -> 0
}

// Branch bodies are still checked: the `false` branch yields 0, so the assertion below must fail
// to verify.
@AlwaysVerify
fun <!VIPER_TEXT!>toIntPositive<!>(b: Boolean): Int {
    val r = when (b) {
        true -> 1
        false -> 0
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r > 0<!>)
    return r
}
