// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.exhale

// Method N — N-4: insufficient basis.
// `inhale(x > 0)` establishes ONLY that `x > 0`. Then `exhale(x > 5)` demands the
// strictly stronger fact `x > 5`, which is NOT provable from `x > 0` (e.g. x == 1
// satisfies the inhaled fact but not x > 5). Silicon must REJECT at the exhale.
// This confirms exhale requires the exact provable fact, not a weaker premise.
@AlwaysVerify
fun <!VIPER_TEXT!>insufficientBasis<!>(x: Int) {
    inhale(x > 0)
    exhale(<!VIPER_VERIFICATION_ERROR!>x > 5<!>)
}
