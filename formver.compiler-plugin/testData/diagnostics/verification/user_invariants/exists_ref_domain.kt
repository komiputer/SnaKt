// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A quantifier variable of reference type carries a runtime-type guard. `exists`
// combines it with `&&`, because a witness must actually lie in the domain, while
// `forAll` combines it with `==>`, so values outside the domain are ignored.

@AlwaysVerify
fun <!VIPER_TEXT!>refExistsNullWitness<!>(): Boolean {
    postconditions<Boolean> {
        exists<Int?> { it == null }
    }
    return true
}

// No value of the domain satisfies the body, so with the `&&` guard there is no
// witness and this must not verify. Had the guard been combined with `==>`, any
// value outside the domain would satisfy the implication vacuously and the
// existential would go through.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>refExistsNoWitnessInDomain<!>(): Boolean {
    postconditions<Boolean> {
        exists<Int?> { it == 1 && it == 2 }
    }
    return true
}<!>

// The dual, for comparison of the emitted Viper text: the same domain under `forAll`
// with a body that holds for every value of the domain.
@AlwaysVerify
fun <!VIPER_TEXT!>refForAllOverSameDomain<!>(): Boolean {
    postconditions<Boolean> {
        forAll<Int?> { (it == 1) implies (it != 2) }
    }
    return true
}
