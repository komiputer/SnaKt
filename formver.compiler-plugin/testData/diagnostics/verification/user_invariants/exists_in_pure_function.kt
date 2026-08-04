// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

@Pure
fun <!VIPER_TEXT!>hasGreaterElement<!>(x: Int): Boolean {
    return exists<Int> { it > x }
}

@AlwaysVerify
fun <!VIPER_TEXT!>callerReliesOnPureExists<!>() {
    verify(hasGreaterElement(0))
}
