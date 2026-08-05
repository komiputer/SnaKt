// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// An existential in a postcondition needs the witness to be carried forward by a
// precondition or a loop invariant. Silicon runs without model-based quantifier
// instantiation, so it will not invent a witness on its own: a bare `exists`
// postcondition does not verify even when it is trivially true.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>bareExistsPostcondition<!>(): Int {
    postconditions<Int> {
        exists<Int> { it == 0 }
    }
    return 0
}<!>

// The same existential verifies once a precondition supplies the witness.
@AlwaysVerify
fun <!VIPER_TEXT!>existsCarriedByPrecondition<!>(): Int {
    preconditions {
        exists<Int> { it == 0 }
    }
    postconditions<Int> {
        exists<Int> { it == 0 }
    }
    return 0
}

// A loop invariant carries the witness through every iteration, so the
// postcondition holds on exit.
@AlwaysVerify
fun <!VIPER_TEXT!>existsCarriedByLoopInvariant<!>(n: Int): Int {
    preconditions {
        n >= 0
    }
    postconditions<Int> {
        exists<Int> { 0 <= it && it <= n }
    }

    var i = 0
    while (i < n) {
        loopInvariants {
            0 <= i && i <= n
            exists<Int> { 0 <= it && it <= i }
        }
        i += 1
    }
    return i
}
