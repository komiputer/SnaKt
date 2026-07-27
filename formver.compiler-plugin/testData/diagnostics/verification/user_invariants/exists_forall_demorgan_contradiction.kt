// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// De Morgan / dual contradiction: the postcondition asserts exists<Int> { P }
// together with forAll<Int> { !P } over the same domain (0 <= it < s.length),
// with the same body P = s[it] == s[0]. Since s.length > 0, index 0 is in
// domain, P(0) is trivially true, so exists P holds -- but forAll !P demands
// !P(0), a direct contradiction. The postcondition is unsatisfiable and the
// verifier should reject it.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>deMorganContradiction<!>(s: String): Int {
    preconditions {
        s.length > 0
    }
    postconditions<Int> {
        exists<Int> { i -> 0 <= i && i < s.length && s[i] == s[0] } &&
                forAll<Int> { i -> !(0 <= i && i < s.length && s[i] == s[0]) }
    }
    return 0
}<!>
