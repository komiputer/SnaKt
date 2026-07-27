// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Both indices into `s` are properly bounded, so the well-formedness objection that the
// unbounded `duplicateIndexExists` case raises does not apply here. `res` itself witnesses
// the existential.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>doubleIndexExistsBounded<!>(s: String, res: Int): Int {
    preconditions {
        0 <= res && res < s.length
    }
    postconditions<Int> {
        exists<Int> { i -> 0 <= i && i < s.length && s[i] == s[res] }
    }
    return 0
}<!>
