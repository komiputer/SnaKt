// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Nested `exists` (QT-2: entirely uncovered in the shipped test data).
// Attempted provable case: for every i, there exists a j with i + j == 4,
// witnessed by j = 4 - i.
//
// RESULT (confirmed, and NOT what was predicted): this FAILS. Generated
// Viper text: `(forall anon_builtin_0: Int ::
// (exists anon_builtin_1: Int :: anon_builtin_0 + anon_builtin_1 == 4))`.
// Mathematically the claim is true for every i, but Silicon cannot
// discharge it. This shows the bare-postcondition-existential grounding
// limitation is broader than just top-level postconditions: it also blocks
// an `exists` nested inside a `forAll`'s body, even though the `forAll`
// itself is auto-triggered on its own bound variable — the automatic
// trigger for the outer `forAll` does not help Z3 instantiate the inner,
// still-bare `exists`. Confirms nested `exists` at least compiles and
// lowers correctly (the Viper text is well-formed and structurally
// correct), but does NOT confirm it verifies correctly when provable —
// that part of the original claim is falsified.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsNestedIntWitnessProvable<!>(): Boolean {
    postconditions<Boolean> {
        forAll<Int> { i -> exists<Int> { j -> i + j == 4 } }
    }
    return true
}<!>

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
