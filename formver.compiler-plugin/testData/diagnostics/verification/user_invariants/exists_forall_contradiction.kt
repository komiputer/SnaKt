// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// `exists<Int> { P }` and `forAll<Int> { !P }` are duals and cannot both hold: the
// postcondition is a direct contradiction and must be rejected.
@AlwaysVerify
fun <!VIPER_TEXT!>existsAndForAllNegation<!>(): Int {
    postconditions<Int> {
        exists<Int> { it == 0 } && forAll<Int> { it != 0 }
    }
    return 0
}
