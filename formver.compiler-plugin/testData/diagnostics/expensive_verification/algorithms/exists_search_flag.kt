// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// `exists` inside a loop invariant, in a different shape from the running-maximum
// loop: a boolean flag whose truth is backed by an existential witness accumulated
// over the prefix scanned so far. The loop runs to completion rather than returning
// early, so the invariant, not the return site, carries the witness.
fun <!VIPER_TEXT!>anyCharacterMatches<!>(s: String, c: Char): Boolean {
    postconditions<Boolean> { res ->
        res implies exists<Int> { 0 <= it && it < s.length && s[it] == c }
    }

    var found = false
    var i = 0
    while (i < s.length) {
        loopInvariants {
            0 <= i && i <= s.length
            found implies exists<Int> { 0 <= it && it < i && s[it] == c }
        }
        if (s[i] == c) {
            found = true
        }
        i += 1
    }
    return found
}
