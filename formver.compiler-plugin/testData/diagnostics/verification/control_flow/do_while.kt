import org.jetbrains.kotlin.formver.plugin.NeverConvert

fun <!VIPER_TEXT!>doWhileSimple<!>(b: Boolean) {
    do {
        val a = 1
        val c = 2
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

fun <!VIPER_TEXT!>doWhileBreakContinue<!>(b: Boolean) {
    do {
        if (b) {
            break
        }
        continue
    } while (b)
}

fun <!VIPER_TEXT!>nestedLabeledDoWhile<!>(b: Boolean) {
    outer@ do {
        do {
            continue@outer
            break@outer
        } while (b)
    } while (b)
}

fun <!VIPER_TEXT!>doWhileInsideWhile<!>(b: Boolean) {
    while (b) {
        do {
        } while (b)
    }
}
