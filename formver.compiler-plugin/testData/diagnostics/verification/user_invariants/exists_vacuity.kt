// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

class Box(val v: Int)

// A contradictory existential is unsatisfiable and must not verify.
@AlwaysVerify
fun <!VIPER_TEXT!>contradictoryExists<!>(): Int {
    postconditions<Int> {
        <!VIPER_VERIFICATION_ERROR!>exists<Int> { it == 0 && it == 1 }<!>
    }
    return 0
}

// A reference-typed bound variable is guarded by a runtime-type test conjoined
// with the body. If the guard were an implication instead, this would verify
// vacuously by choosing a witness that is not a `Box`, which would let any
// existential over a class type be proved for free.
@AlwaysVerify
fun <!VIPER_TEXT!>referenceTypedExistsIsNotVacuous<!>(): Int {
    postconditions<Int> {
        <!VIPER_VERIFICATION_ERROR!>exists<Box> { 1 == 2 }<!>
    }
    return 0
}

// The same shape stated over a precondition: an unsatisfiable existential over a
// class type must not be assumable, so it cannot be re-established downstream.
@AlwaysVerify
fun <!VIPER_TEXT!>referenceTypedExistsInPrecondition<!>(): Int {
    preconditions {
        exists<Box> { 1 == 2 }
    }
    postconditions<Int> {
        exists<Box> { 1 == 2 }
    }
    return 0
}

// An existential does not license a universal: knowing that some integer is
// positive says nothing about all of them.
@AlwaysVerify
fun <!VIPER_TEXT!>existsDoesNotImplyForAll<!>(): Int {
    preconditions {
        exists<Int> { it > 0 }
    }
    postconditions<Int> {
        <!VIPER_VERIFICATION_ERROR!>forAll<Int> { it > 0 }<!>
    }
    return 0
}
