// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.exhale
import org.jetbrains.kotlin.formver.plugin.verify

// Method A — Feature Contract, adversarial should-PASS programs for inhale/exhale.
// Each function's generated Viper is captured in the golden file; inspect it to
// confirm the required keyword (inhale / exhale / assert) for each property.

// A-1: inhale translation fidelity. Varied pure boolean expressions must each
// produce a Viper `inhale <c>` (not assert, not exhale). We inhale several
// distinct shapes: strict/loose comparison, inequality, conjunction, disjunction.
@AlwaysVerify
fun <!VIPER_TEXT!>a1InhaleVariedShapes<!>(x: Int, y: Int) {
    inhale(x > 0)
    inhale(y <= 100)
    inhale(x != y)
    inhale(x > 0 && y > 0)
    inhale(x < 0 || x > 0)
}

// A-2: exhale translation fidelity — distinct from assert. `c` is provable from
// the inhaled premise, so the exhale asserts-and-transfers successfully. The
// golden file MUST show `exhale`, never `assert`, for these calls.
@AlwaysVerify
fun <!VIPER_TEXT!>a2ExhaleIsNotAssert<!>(x: Int) {
    inhale(x > 10)
    exhale(x > 5)
    inhale(x > 10)
    exhale(x != 0)
}

// A-3: inhale enables a downstream verify that is NOT provable otherwise.
// Without `inhale(x > 10)` an unconstrained x could be 0 or <= 5, so both
// verifies would fail. Silicon must accept purely because of the inhale.
@AlwaysVerify
fun <!VIPER_TEXT!>a3InhaleEnablesVerify<!>(x: Int) {
    inhale(x > 10)
    verify(x > 5)
    verify(x != 0)
}

// A-4 (should-PASS portion): exhale succeeds when `c` is provable. The removal
// semantics (should-FAIL) live in Method N (N-2). Here we only confirm the
// exhale of a provable, established fact verifies.
@AlwaysVerify
fun <!VIPER_TEXT!>a4ExhaleProvableSucceeds<!>(x: Int) {
    inhale(x >= 1)
    exhale(x >= 1)
}

// A-5: sequential inhale/exhale chains. Multiple inhaled facts are all available
// for a later combined exhale; then a fresh inhale/exhale pair follows. Order
// must be preserved in the generated Viper.
@AlwaysVerify
fun <!VIPER_TEXT!>a5SequentialChain<!>(a: Int, b: Int) {
    inhale(a > 0)
    inhale(b > 0)
    exhale(a > 0 && b > 0)
    inhale(a > -1)
    exhale(a > -1)
}

// A-5 (second variant): interleaved inhale/exhale where an exhale relies on a
// fact inhaled two steps earlier, and a following exhale relies on the latest.
@AlwaysVerify
fun <!VIPER_TEXT!>a5Interleaved<!>(x: Int) {
    inhale(x > 0)
    exhale(x > 0)
    inhale(x > -1)
    exhale(x > -1)
}

// A-6: verify still produces `assert`, exhale produces `exhale`, in the SAME
// function. The golden file must show BOTH keywords, proving the two DSL
// functions remain distinct embeddings.
@AlwaysVerify
fun <!VIPER_TEXT!>a6VerifyAssertVsExhale<!>(x: Int) {
    inhale(x > 3)
    verify(x > 0)
    exhale(x > 0)
}

// A-7: purity constraint — pure arithmetic, comparisons and logical operators
// are accepted as arguments to both inhale and exhale, compile, and verify.
@AlwaysVerify
fun <!VIPER_TEXT!>a7PureArgumentsAccepted<!>(x: Int, y: Int) {
    inhale(x + 1 > x)
    inhale((x * 2) - x == x)
    inhale(x > y || y >= x)
    exhale(x + 1 > x)
    exhale(x > y || y >= x)
}

// A-8 (should-PASS portion): inhale(c); exhale(c); verify(c) — the FAILING case
// belongs to Method N (N-2). Method A commits only the should-PASS shape:
// inhale a fact and verify it BEFORE exhaling, confirming the fact was live
// while it existed. The verify precedes the exhale so it is provable.
@AlwaysVerify
fun <!VIPER_TEXT!>a8FactLiveBeforeExhale<!>(x: Int) {
    inhale(x > 7)
    verify(x > 7)
    exhale(x > 7)
}
