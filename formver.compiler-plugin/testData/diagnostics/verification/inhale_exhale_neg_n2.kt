// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.inhale
import org.jetbrains.kotlin.formver.plugin.exhale
import org.jetbrains.kotlin.formver.plugin.verify

// Method N — N-2: post-exhale reuse of an exhaled boolean fact.
//   inhale(c)  -> establishes c in the proof state
//   exhale(c)  -> emits Viper `exhale c` (asserts c holds); translation is correct
//   verify(c)  -> asserts c again; this PASSES (in-contract, correct behavior)
//
// This is correct, in-contract behavior. Viper's `exhale` is designed to consume
// permission-typed resources (`acc(...)`). For pure boolean conditions — the only
// input class this feature supports — `exhale c` checks the condition but does not
// remove the boolean fact from the path condition. Pure boolean facts are not
// consumable resources in Viper's permission model, so they remain available after
// exhale. The plugin translation is faithful (emits `exhale`, not `assert`); the
// observed behavior reflects Viper/Silicon semantics, not a plugin bug.
// The golden file records the correct accepting behavior.
@AlwaysVerify
fun <!VIPER_TEXT!>postExhaleReuse<!>(c: Boolean) {
    inhale(c)
    exhale(c)
    verify(c)
}
