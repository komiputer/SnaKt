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
//
// Actual observed outcome: this fails with "Postcondition ... might not hold.
// Assertion (exists ... ) might not hold" -- the same generic diagnostic shape
// a bare postcondition existential produces with no distinguishing crash- or
// Silver-info-kind-specific wording. With both indices bounded, this case
// gives no evidence that Info.fromSilver's NoInfo fallback is exercised here;
// it looks identical to the mundane bare-existential-doesn't-verify category,
// consistent with the concern in DI-1 that the original crash fix may have no
// surviving regression coverage.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>doubleIndexExistsBothBounded<!>(s: String, res: Int): Int {
    preconditions {
        0 <= res && res < s.length
    }
    postconditions<Int> {
        exists<Int> { i -> 0 <= i && i < s.length && s[i] == s[res] }
    }
    return 0
}<!>
