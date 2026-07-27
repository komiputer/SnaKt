// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Nesting one quantifier inside another's body, in both orders. Each inner
// quantifier must lower to its own Viper quantifier with its own bound variable, and
// the inner body must be able to refer to the outer bound variable.

@AlwaysVerify
fun <!VIPER_TEXT!>nestedExistsExists<!>(): Int {
    postconditions<Int> {
        exists<Int> { i -> exists<Int> { j -> i > 0 && j > 0 && i + j == 5 } }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>forAllHasLargerWitness<!>(): Int {
    postconditions<Int> {
        forAll<Int> { i -> exists<Int> { j -> j > i } }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsUpperBoundOfNegatives<!>(): Int {
    postconditions<Int> {
        exists<Int> { b -> forAll<Int> { i -> (i < 0) implies (i < b) } }
    }
    return 0
}
