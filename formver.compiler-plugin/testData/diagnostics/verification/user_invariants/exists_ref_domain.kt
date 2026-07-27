// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A quantifier variable of reference type carries a runtime-type guard. `exists`
// combines it with `&&`, because a witness must actually lie in the domain, while
// `forAll` combines it with `==>`, so values outside the domain are ignored. The
// generated Viper text pinned in the golden files is what establishes that; all three
// functions fail to verify, so the verification outcomes on their own do not
// distinguish the two combinators.

<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>refExistsNullWitness<!>(): Boolean {
    postconditions<Boolean> {
        exists<Int?> { it == null }
    }
    return true
}<!>

// No value of the domain satisfies the body, so with the `&&` guard there is no
// witness. Had the guard been combined with `==>` instead, any value outside the
// domain would satisfy the implication vacuously and the existential would hold.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>refExistsNoWitnessInDomain<!>(): Boolean {
    postconditions<Boolean> {
        exists<Int?> { it == 1 && it == 2 }
    }
    return true
}<!>

// The dual, for comparison of the emitted Viper text: the same domain under `forAll`
// with a body that holds for every value of the domain.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>refForAllOverSameDomain<!>(): Boolean {
    postconditions<Boolean> {
        forAll<Int?> { (it == 1) implies (it != 2) }
    }
    return true
}<!>
