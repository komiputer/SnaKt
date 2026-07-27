// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Intended contract: a `Char`-typed quantifier variable ranges over the Unicode
// code-point range [0, 65536). `Char` lowers to a Viper `Int`, so without a domain
// bound the variable also ranges over negative and over-large code points.
//
// Both functions turn on that bound rather than on any particular witness: a bounded
// `Char` domain has a least and a greatest element, while the unbounded Viper `Int`
// domain has neither. Under the intended contract both would verify. Neither does,
// and the golden files show why: the quantified variable is emitted as a bare
// `Int` with no `0 <= x && x < 65536` guard.

<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>leastCharExists<!>(): Boolean {
    postconditions<Boolean> {
        exists<Char> { lo -> forAll<Char> { c -> lo <= c } }
    }
    return true
}<!>

<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>greatestCharExists<!>(): Boolean {
    postconditions<Boolean> {
        exists<Char> { hi -> forAll<Char> { c -> c <= hi } }
    }
    return true
}<!>
