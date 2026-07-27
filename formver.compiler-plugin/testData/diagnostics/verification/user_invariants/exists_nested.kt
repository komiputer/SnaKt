// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Nested `exists` (an existential inside another existential's body), entirely
// uncovered by the existing test data. The inner witness is unconstrained other
// than being equal to the outer bound variable, so this should lower to a sensible
// nested Viper `exists` and verify trivially.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>nestedExistsWitness<!>(s: String): Boolean {
    preconditions {
        s.length > 0
    }
    postconditions<Boolean> {
        exists<Int> { i ->
            0 <= i && i < s.length && exists<Int> { j -> j == i }
        }
    }
    return true
}<!>
