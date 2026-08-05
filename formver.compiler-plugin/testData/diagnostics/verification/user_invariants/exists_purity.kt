// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

class Counter(var x: Int)

fun <!VIPER_TEXT!>impureHelper<!>(): Int {
    return 1
}

// An existential body must be pure, as Viper's `exists` requires. A side effect
// in the body has to be rejected with a purity diagnostic; accepting it would
// let a specification mutate state while being evaluated.
@NeverVerify
fun <!VERIFICATION_SKIPPED!>existsWithSideEffectingBody<!>(c: Counter): Int {
    preconditions {
        exists<Int> { <!PURITY_VIOLATION!>it == c.x++<!> }
    }
    return 0
}

// Calling a non-pure function from inside the body is the same violation.
@NeverVerify
fun <!VERIFICATION_SKIPPED!>existsCallingImpureFunction<!>(): Int {
    preconditions {
        exists<Int> { <!PURITY_VIOLATION!>it == impureHelper()<!> }
    }
    return 0
}
