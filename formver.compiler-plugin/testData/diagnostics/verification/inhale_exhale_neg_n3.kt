// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.verify

// Method N — N-3: `inhale(false)` soundness escape hatch (in-contract, NOT a bug).
// This is deliberately included to document the soundness boundary. `false` is a
// pure boolean literal, so inhale(false) must NOT be a compile-time / purity error.

// Variant 3a (should PASS vacuously): once `false` is inhaled into the proof state,
// EVERYTHING is provable by contradiction. So verify(1 == 2) — a logically false
// condition — is accepted by Silicon. There should be NO VIPER_VERIFICATION_ERROR
// marker here: the verify succeeds precisely because the state is inconsistent.
@AlwaysVerify
fun <!VIPER_TEXT!>inhaleFalseVacuous<!>() {
    inhale(false)
    verify(1 == 2)
}

// Variant 3b (in-contract documentation case): `inhale(false)` is the documented
// soundness ESCAPE HATCH. Anything after it verifies vacuously because the proof
// state is contradictory. Callers must treat inhale(false) as an explicit,
// intentional "trust me" marker — it silently makes the method verify no matter
// what follows. Here we inhale false and then verify several mutually inconsistent
// facts, all of which pass vacuously.
@AlwaysVerify
fun <!VIPER_TEXT!>inhaleFalseEscapeHatch<!>(x: Int) {
    inhale(false)
    // All of the following verify vacuously — the state is already contradictory.
    verify(x > 0)
    verify(x < 0)
    verify(x == 0)
}
