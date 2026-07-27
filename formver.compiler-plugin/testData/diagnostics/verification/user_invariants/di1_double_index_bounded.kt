// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// DI-1 literal reproduction: the original exists_double_index scenario indexed
// s at both the exists-bound variable i and the free parameter res, with res
// left unbounded, which the current test suite flags as a possibly-negative
// index -- a mundane well-formedness error, not necessarily the original
// unhandled-Silver-info-kind crash path. Here both i and res are properly
// bounded (0 <= i < s.length via the exists guard, 0 <= res < s.length via the
// precondition), removing the mundane out-of-bounds failure so any remaining
// verification failure must come from the existential itself.
@AlwaysVerify
fun doubleIndexExistsBothBounded(s: String, res: Int): Int {
    preconditions {
        0 <= res && res < s.length
    }
    postconditions<Int> {
        exists<Int> { i -> 0 <= i && i < s.length && s[i] == s[res] }
    }
    return 0
}
