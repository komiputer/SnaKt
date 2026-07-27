// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Minimal pair pinning the bare-postcondition-existential limitation
// (observed independently across multiple pipeline solvers): a standalone
// postcondition existential offers Silicon/Z3 no ground term to instantiate
// against and is expected to fail even when the underlying property is
// true and trivially witnessed by a fixed constant. This is suspected to be
// a systemic verifier-side limitation, not evidence of an `exists`-specific
// defect — see the other two functions below for the same property proven
// two different ways once a ground witness is available at the proof site.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsZeroWitnessBarePostcondition<!>(): Int {
    postconditions<Int> {
        exists<Int> { it == 0 }
    }
    return 0
}<!>

// Same property (a witness 0 exists), but the witness is ground/concrete at
// the return site via the function's own return value binding `res`.
// Expected to verify if referencing `res` in the body gives Z3 a term to
// instantiate against.
@AlwaysVerify
fun <!VIPER_TEXT!>existsZeroWitnessGroundAtReturn<!>(): Int {
    postconditions<Int> { res ->
        exists<Int> { it == res }
    }
    return 0
}

// Same property again, proven via a loop invariant that carries the exact
// witness (0) forward to the postcondition, mirroring the
// loop-invariant-carried pattern already used successfully in
// max_character.kt and exists_counting_loop.kt. Expected to verify.
@AlwaysVerify
fun <!VIPER_TEXT!>existsZeroWitnessGroundViaLoop<!>(): Int {
    postconditions<Int> {
        exists<Int> { it == 0 }
    }
    var i = 0
    while (i < 1) {
        loopInvariants {
            0 <= i && i <= 1
            i == 1 implies exists<Int> { it == 0 }
        }
        i += 1
    }
    return 0
}
