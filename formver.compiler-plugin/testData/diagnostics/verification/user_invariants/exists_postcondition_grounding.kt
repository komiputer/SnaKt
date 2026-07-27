// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Minimal three-way differential pinning the bare-postcondition-existential
// limitation (observed independently across multiple pipeline solvers), for
// the same property (a witness 0 exists) in three contexts:
//
//   (a) bare postcondition — expected to FAIL: Silicon/Z3 has no ground
//       term to instantiate the existential against.
//   (b) bare precondition (below, existsZeroWitnessBarePrecondition) —
//       expected to PASS, but VACUOUSLY: a precondition is assumed at the
//       body's entry, never proven satisfiable at the definition site. This
//       is the same shape as the shipped `simpleExists` case in exists.kt
//       (lines 6-11), the only other precondition-existential in the whole
//       corpus alongside max_character.kt. Its green status is NOT evidence
//       that Silicon can discharge an existential; it only demonstrates the
//       assumption path, which never calls the solver's existential
//       instantiation machinery at all.
//   (c) ground witness in context (existsZeroWitnessGroundAtReturn /
//       existsZeroWitnessGroundViaLoop below) — expected to PASS
//       meaningfully, because a concrete term is already in scope for Z3 to
//       instantiate against.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsZeroWitnessBarePostcondition<!>(): Int {
    postconditions<Int> {
        exists<Int> { it == 0 }
    }
    return 0
}<!>

// Leg (b): same property as a bare precondition. Expected to verify, but
// only vacuously — see the block comment above.
@AlwaysVerify
fun <!VIPER_TEXT!>existsZeroWitnessBarePrecondition<!>(): Int {
    preconditions {
        exists<Int> { it == 0 }
    }
    return 0
}

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
