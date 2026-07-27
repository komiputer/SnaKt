// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A quantifier variable over a genuine reference type (a user-defined class, not
// Int/Char/Boolean/String, none of which are backed by a runtime-type injection)
// must combine the runtime-type predicate with the body using `&&`, not `==>`:
// the witness must actually inhabit the domain, unlike the universal case where
// out-of-domain elements would vacuously satisfy an implication.
class Box(val v: Int)

@AlwaysVerify
fun <!VIPER_TEXT!>existsBoxWitness<!>(b: Box): Int {
    postconditions<Int> {
        exists<Box> { true }
    }
    return 0
}
