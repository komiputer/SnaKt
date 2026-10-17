// FULL_JDK
// needed for Comparable resolution on `x > 0` below

import org.jetbrains.kotlin.formver.plugin.NeverConvert

fun <!VIPER_TEXT!>doWhileSimple<!>(b: Boolean) {
    do {
        val a = 1
    } while (b)
}

@NeverConvert
fun returnsBoolean(): Boolean {
    return false
}

fun <!VIPER_TEXT!>doWhileFunctionCondition<!>() {
    do {
    } while (returnsBoolean())
}

@NeverConvert
fun computeX(): Int {
    return 0
}

fun <!VIPER_TEXT!>doWhileConditionUsesBodyLocal<!>() {
    do {
        val x = computeX()
    } while (x > 0)
}

fun <!VIPER_TEXT!>doWhileBreakContinue<!>(b: Boolean) {
    do {
        if (b) {
            break
        }
        continue
    } while (b)
}

fun <!VIPER_TEXT!>doWhileLabelledOuterBreak<!>(b: Boolean) {
    outer@ do {
        do {
            break@outer
        } while (b)
    } while (b)
}

fun <!VIPER_TEXT!>doWhileReturn<!>(b: Boolean): Int {
    do {
        return 0
    } while (b)
    return 1
}
