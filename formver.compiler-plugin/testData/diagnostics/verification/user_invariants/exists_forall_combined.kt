// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Second, independent example combining `forAll` and `exists` in the same
// specification (QT-2), distinct from max_character.kt: a linear search
// stated as a biconditional between the boolean result and existence of a
// matching index, with the negative branch stated via `forAll`. Also
// exercises `exists` inside `loopInvariants { }` with an early-exit search
// loop shape, distinct from max_character.kt's running-maximum loop.
@AlwaysVerify
fun <!VIPER_TEXT!>stringContainsCorrespondsToExists<!>(s: String, target: Char): Boolean {
    postconditions<Boolean> { res ->
        (res implies exists<Int> { 0 <= it && it < s.length && s[it] == target }) &&
            (!res implies forAll<Int> { (0 <= it && it < s.length) implies (s[it] != target) })
    }

    var i = 0
    var found = false
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            found implies exists<Int> { 0 <= it && it < i && s[it] == target }
            !found implies forAll<Int> { (0 <= it && it < i) implies (s[it] != target) }
        }
        if (s[i] == target) {
            found = true
        }
        i += 1
    }
    return found
}
