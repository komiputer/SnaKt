// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

@AlwaysVerify
fun <!VIPER_TEXT!>someIntegerIsAtLeastRes<!>(): Int {
    postconditions<Int> { res ->
        exists<Int> {
            it >= res
        }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsWithConjunctiveBody<!>(): Int {
    postconditions<Int> { res ->
        exists<Int> {
            it > 0
            it < 10
        }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsInPrecondition<!>(n: Int) {
    preconditions {
        exists<Int> {
            it * it == n
        }
    }
    verify(true)
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsInLoopInvariant<!>(): Int {
    var i = 0
    while (i < 10) {
        loopInvariants {
            exists<Int> { it >= i }
        }
        i = i + 1
    }
    return i
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsComposedWithNonQuantifiedConjunct<!>(res: Int) {
    verify((res >= 0 || res < 0) && exists<Int> { it >= res })
    verify(true implies exists<Int> { it >= res })
}
