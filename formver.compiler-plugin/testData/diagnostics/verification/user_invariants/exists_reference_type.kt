// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

class RefWitness()

// Adversarial case for the reference-type domain guard (QT-1 / API doc §3):
// `exists<T>` over a reference type conjoins the runtime-type predicate
// isOf(x, T) with the body via `&&`, whereas `forAll<T>` uses `==>`. This
// probes the conjunction with a nullable reference-typed witness: a null
// value trivially satisfies `it == null`, but it is unclear from reading the
// code alone whether isOf(x, T) holds for a null witness. If isOf(null, T)
// does not hold, the conjunction makes this existential unprovable despite
// the body being trivially satisfiable — exactly the kind of crack the &&
// vs ==> distinction should be checked against.
@AlwaysVerify
fun <!VIPER_TEXT!>existsNullableRefWitnessIsNull<!>(x: RefWitness?): Boolean {
    postconditions<Boolean> {
        exists<RefWitness?> { it == null }
    }
    return true
}
