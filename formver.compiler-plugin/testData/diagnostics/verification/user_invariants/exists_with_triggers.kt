// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

fun <!VIPER_TEXT!>existsWithSimpleTrigger<!>(): Int {
    postconditions<Int> { res ->
        exists<Int> {
            // Specify trigger expression to guide SMT solver
            triggers(it * it)
            it * it >= 0
            it * it >= res
        }
    }
    return 0
}

fun <!VIPER_TEXT!>existsWithMultipleTriggers<!>(): Int {
    postconditions<Int> { res ->
        exists<Int> {
            // Multiple trigger expressions can be provided
            triggers(it * it, it + 1)
            (it != 0) implies (it * it >= res)
        }
    }
    return 1
}

fun <!VIPER_TEXT!>existsWithTriggerInLoop<!>(str: String): Int {
    var res = 0
    var i = 10
    while (i > 0) {
        loopInvariants {
            exists<Int> {
                // Triggers can be used in loop invariants
                triggers(it + str.length)
                it < 0
            }
        }
        i--
    }
    return res
}

fun <!VIPER_TEXT!>existsWithoutTriggers<!>(): Int {
    postconditions<Int> { res ->
        exists<Int> {
            // exists without triggers still works (automatic trigger inference)
            it * it >= 0
        }
    }
    return 0
}
