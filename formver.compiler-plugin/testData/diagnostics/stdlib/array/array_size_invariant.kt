// FULL_JDK
// WITH_STDLIB

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

@AlwaysVerify
fun <!VIPER_TEXT!>intArraySizeNonNegative<!>(a: IntArray): Int {
    return a.size
}

@AlwaysVerify
fun <!VIPER_TEXT!>booleanArraySizeNonNegative<!>(a: BooleanArray): Int {
    return a.size
}

@AlwaysVerify
fun <!VIPER_TEXT!>genericArraySizeNonNegative<!>(a: Array<Int>): Int {
    return a.size
}
