// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Second, independent example of `exists` inside `loopInvariants { }`
// (QT-2), distinct from both max_character.kt's running-maximum loop and
// exists_forall_combined.kt's early-exit search loop: a counting loop that
// never exits early, where the invariant carries forward a witness once the
// running count becomes positive.
//
// RESULT (confirmed, and NOT what was predicted): this FAILS to verify
// ("Postcondition ... might not hold"), even though the loop invariant
// carries exactly the needed witness fact up to loop exit (where `i ==
// s.length`, making the invariant's existential syntactically the
// postcondition's existential). Compare with max_character.kt, whose
// structurally similar loop-invariant-carried `exists` DOES verify: the
// difference is that max_character.kt's invariant states the existential
// UNCONDITIONALLY (`exists<Int> { ... }`), whereas this one wraps it in an
// implication guard (`positive implies exists<Int> { ... }`). This narrows
// the bare-postcondition-existential grounding limitation found elsewhere in
// this iteration: the loop-invariant-carried pattern is not a reliable
// workaround in general -- it only sidesteps the limitation when the
// existential is asserted unconditionally, not when it sits behind an
// `implies` guard, even though the guard's condition is known true at the
// point that matters.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>countOfAsPositiveImpliesWitnessExists<!>(s: String, target: Char): Boolean {
    postconditions<Boolean> { res ->
        (res implies exists<Int> { 0 <= it && it < s.length && s[it] == target })
    }

    var count = 0
    var i = 0
    var positive = false
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            positive implies exists<Int> { 0 <= it && it < i && s[it] == target }
        }
        if (s[i] == target) {
            count += 1
            positive = true
        }
        i += 1
    }
    return count > 0
}<!>
