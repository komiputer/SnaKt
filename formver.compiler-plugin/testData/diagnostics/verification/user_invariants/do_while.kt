// FULL_JDK
// REPLACE_STDLIB_EXTENSIONS

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.loopInvariants
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun <!VIPER_TEXT!>bodyRunsBeforeCondition<!>() {
    var x = 0
    do {
        loopInvariants { x <= 1 }
        x = 1
    } while (false)
    verify(x == 1)
}

@AlwaysVerify
fun <!VIPER_TEXT!>invariantFailsOnEntry<!>() {
    var i = 0
    <!VIPER_VERIFICATION_ERROR!>do {
        loopInvariants { i >= 1 }
        i = i + 1
    } while (i < 10)<!>
    verify(i == 10)
}
