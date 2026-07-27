// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// `exists` inside `loopInvariants { }`, a second independent example distinct from
// max_character.kt's counting loop: a linear search that carries a found-index
// witness forward through the loop instead of a running maximum.
fun <!VIPER_TEXT!>containsChar<!>(s: String, target: Char): Boolean {
    postconditions<Boolean> { res ->
        res implies exists<Int> { 0 <= it && it < s.length && s[it] == target }
    }

    var i = 0
    var found = false
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            found implies exists<Int> { 0 <= it && it < i && s[it] == target }
        }
        if (s[i] == target) {
            found = true
        }
        i += 1
    }
    return found
}
