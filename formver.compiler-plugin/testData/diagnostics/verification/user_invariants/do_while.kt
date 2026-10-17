// FULL_JDK
// REPLACE_STDLIB_EXTENSIONS

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.loopInvariants
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun <!VIPER_TEXT!>doWhileVerifies<!>(n: Int) {
    preconditions {
        n > 0
    }
    var it = 0
    do {
        loopInvariants {
            it < n
        }
        it = it + 1
    } while (it < n)
    verify(it == n)
}

@AlwaysVerify
fun <!VIPER_TEXT!>doWhileInvariantBroken<!>(n: Int) {
    preconditions {
        n > 0
    }
    var it = 0
    <!VIPER_VERIFICATION_ERROR!>do {
        loopInvariants {
            it == 0
        }
        it = it + 1
    } while (it < n)<!>
}

@AlwaysVerify
fun <!VIPER_TEXT!>doWhileBodyCannotAssumeCondition<!>(n: Int) {
    preconditions {
        n > 0
    }
    var it = n
    do {
        verify(<!VIPER_VERIFICATION_ERROR!>it < n<!>)
        it = it + 1
    } while (it < n)
}

@AlwaysVerify
fun <!VIPER_TEXT!>doWhileInvariantNotCheckedOnExit<!>(n: Int) {
    preconditions {
        n > 0
    }
    var it = 0
    do {
        loopInvariants {
            it < n
        }
        it = it + 1
    } while (it < n)
}

@AlwaysVerify
fun <!VIPER_TEXT!>doWhileInvariantCheckedBeforeFirstIteration<!>(n: Int) {
    preconditions {
        n > 0
    }
    var it = 0
    <!VIPER_VERIFICATION_ERROR!>do {
        loopInvariants {
            it > 0
        }
        it = it + 1
    } while (it < n)<!>
}
