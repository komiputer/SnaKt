// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Intended contract: a `Char`-typed quantifier variable ranges over the Unicode
// code-point range [0, 65536). `Char` lowers to a Viper `Int`, so without a domain
// bound the variable also ranges over negative and over-large code points.
//
// Both functions below turn on that bound rather than on any particular witness. A
// bounded `Char` domain has a least and a greatest element; the unbounded Viper `Int`
// domain has neither, so under the intended contract both must verify and without the
// bound both are false.

@AlwaysVerify
fun <!VIPER_TEXT!>leastCharExists<!>(): Boolean {
    postconditions<Boolean> {
        exists<Char> { lo -> forAll<Char> { c -> lo <= c } }
    }
    return true
}

@AlwaysVerify
fun <!VIPER_TEXT!>greatestCharExists<!>(): Boolean {
    postconditions<Boolean> {
        exists<Char> { hi -> forAll<Char> { c -> c <= hi } }
    }
    return true
}
