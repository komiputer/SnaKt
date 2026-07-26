// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.exhale
import org.jetbrains.kotlin.formver.plugin.verify

// Method N — N-2: post-exhale reuse of an exhausted fact.
// THIS IS THE DECISIVE TEST that `exhale` is NOT `assert`.
//   inhale(c)  -> establishes c in the proof state
//   exhale(c)  -> asserts c (succeeds) AND removes/transfers c out of the state
//   verify(c)  -> tries to re-assert c, which is no longer available
// If exhale were merely `assert`, `c` would still be present and verify(c) would
// PASS. Correct exhale semantics REMOVE `c`, so the verify(c) was EXPECTED to be
// REJECTED by Silicon.
//
// [UNVERIFIED] ACTUAL RESULT: verify(c) is ACCEPTED (no VIPER_VERIFICATION_ERROR is
// produced — see the golden file, which contains generated Viper `inhale c; exhale
// c; assert c` but NO .viper.diag.txt). The translation is faithful (`exhale c`,
// not `assert c`), so this is NOT a plugin translation bug. Rather, it is a
// property of Viper/Silicon semantics: `exhale` only CONSUMES permission
// assertions (`acc(...)`); a PURE boolean assertion is checked but its logical
// fact is NOT removed from the path condition. Because this feature supports only
// pure boolean conditions (permissions are explicitly out of scope per the API
// surface), `exhale(c)` is observationally EQUIVALENT to `assert(c)` for every
// input the feature accepts — so the headline "exhale is not assert" distinction
// is not observable at the source level. Flagged CRITICAL per the N-2 brief: a
// should-fail case that verifies. The golden file below records the ACTUAL
// (accepting) behavior so the test is stable.
@AlwaysVerify
fun <!VIPER_TEXT!>postExhaleReuse<!>(c: Boolean) {
    inhale(c)
    exhale(c)
    verify(c)
}
