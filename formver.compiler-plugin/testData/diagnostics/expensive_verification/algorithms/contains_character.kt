// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A linear search stating both directions of its result in one postcondition: on
// success a witness index exists, on failure no index matches. The existential is
// discharged at the early return, where the loop counter is the witness, and the
// universal is discharged at the end from the loop invariant.
fun <!VIPER_TEXT!>containsCharacter<!>(s: String, c: Char): Boolean {
    postconditions<Boolean> { res ->
        (res implies exists<Int> { 0 <= it && it < s.length && s[it] == c }) &&
                ((!res) implies forAll<Int> { (0 <= it && it < s.length) implies (s[it] != c) })
    }

    var i = 0
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            forAll<Int> { (0 <= it && it < i) implies (s[it] != c) }
        }
        if (s[i] == c) {
            return true
        }
        i += 1
    }
    return false
}
