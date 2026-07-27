// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

class RefWitness()

// Adversarial case for the reference-type domain guard (QT-1 / API doc §3):
// `exists<T>` over a reference type conjoins the runtime-type predicate
// isOf(x, T) with the body via `&&`, whereas `forAll<T>` uses `==>`. This
// probes the conjunction with a nullable reference-typed witness: a null
// value trivially satisfies `it == null`, but it is unclear from reading the
// code alone whether isOf(x, T) holds for a null witness.
//
// RESULT (confirmed): fails to verify. The generated Viper text is
// `exists anon: Ref :: { isSubtype(typeOf(anon), nullable(RefWitness())) }
// isSubtype(typeOf(anon), nullable(RefWitness())) && anon == nullValue()`,
// which confirms the `&&` conjunction shape claimed in the API doc (not
// `==>`), and shows isOriginallyRef's `isOf` guard is lowered as a
// `isSubtype(..., nullable(...))` check rather than an unqualified type
// check. However, this is a BARE POSTCONDITION existential, which per
// exists_postcondition_grounding.kt fails independent of its content
// (Silicon cannot discharge a bare postcondition existential at all in this
// codebase's current configuration). This case therefore CONFIRMS the &&
// shape empirically for the first time, but is INCONCLUSIVE on whether
// isOf(null, T) specifically holds — the grounding limitation confounds the
// result before that question can be isolated.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsNullableRefWitnessIsNull<!>(x: RefWitness?): Boolean {
    postconditions<Boolean> {
        exists<RefWitness?> { it == null }
    }
    return true
}<!>
