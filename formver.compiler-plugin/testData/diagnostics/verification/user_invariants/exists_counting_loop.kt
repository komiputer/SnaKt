// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Second, independent example of `exists` inside `loopInvariants { }`
// (QT-2), distinct from both max_character.kt's running-maximum loop and
// exists_forall_combined.kt's early-exit search loop: a counting loop that
// never exits early, where the invariant carries forward a witness once the
// running count becomes positive.
@AlwaysVerify
fun <!VIPER_TEXT!>countOfAsPositiveImpliesWitnessExists<!>(s: String): Boolean {
    postconditions<Boolean> { res ->
        res implies exists<Int> { 0 <= it && it < s.length && s[it] == 'a' }
    }

    var count = 0
    var i = 0
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            count > 0 implies exists<Int> { 0 <= it && it < i && s[it] == 'a' }
        }
        if (s[i] == 'a') {
            count += 1
        }
        i += 1
    }
    return count > 0
}
