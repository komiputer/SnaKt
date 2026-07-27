// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Nested `exists` (QT-2: entirely uncovered in the shipped test data).
// A provable case: for every i, there exists a j with i + j == 4, witnessed
// by j = 4 - i. Confirms nested `exists` at least compiles, lowers, and
// verifies correctly when provable.
@AlwaysVerify
fun <!VIPER_TEXT!>existsNestedIntWitnessProvable<!>(): Boolean {
    postconditions<Boolean> {
        forAll<Int> { i -> exists<Int> { j -> i + j == 4 } }
    }
    return true
}

// An unprovable nested claim: that any given string of unconstrained content
// necessarily contains two equal characters at distinct positions. Nothing
// in the signature guarantees this (e.g. it is false for a two-character
// string with distinct characters), so this should fail to verify. Confirms
// nested `exists` also correctly propagates verification failures, not just
// successes.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsNestedStringDuplicateUnprovable<!>(s: String): Boolean {
    postconditions<Boolean> {
        exists<Int> { i -> 0 <= i && i < s.length && exists<Int> { j -> 0 <= j && j < s.length && i != j && s[i] == s[j] } }
    }
    return true
}<!>
