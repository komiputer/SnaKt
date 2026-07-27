// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// The index is derived by arithmetic on the bound variable rather than being the bound
// variable itself: `i - 1` can be -1 even though `i` is guarded from below, so the string
// access inside `exists` is not well-formed.
@AlwaysVerify
fun <!VIPER_TEXT!>predecessorIndexExists<!>(s: String): Int {
    postconditions<Int> {
        exists<Int> { i -> 0 <= i && i < s.length && s[i] == s[i - 1] }
    }
    return 0
}

// The mirror case at the upper end: `i + 1` can equal `s.length`.
@AlwaysVerify
fun <!VIPER_TEXT!>successorIndexExists<!>(s: String): Int {
    postconditions<Int> {
        exists<Int> { i -> 0 <= i && i < s.length && s[i] == s[i + 1] }
    }
    return 0
}
