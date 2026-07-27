// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A second unguarded-index shape distinct from duplicateIndexExists in exists.kt:
// here the index is derived by arithmetic on the bound variable (i + 1) rather
// than being the bound variable itself. The guard 0 <= i && i < s.length bounds
// i but not i + 1, which can reach s.length -- one past the end of the string --
// so this should be flagged as a possibly-out-of-bounds (upper bound) index
// well-formedness error inside `exists`.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>arithmeticShiftIndexExists<!>(s: String): Int {
    postconditions<Int> {
        exists<Int> { i -> 0 <= i && i < s.length && s[i + 1] == s[i] }
    }
    return 0
}<!>
