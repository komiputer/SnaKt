// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Pair2
class Left(val v: Int) : Pair2
class Right(val v: Int) : Pair2

// Adversarial (Issue 1): the second `is Left` branch is redundant. FIR is expected
// to classify this `when` as `RedundantlyExhaustive` rather than `ProperlyExhaustive`.
// The current code only treats `ProperlyExhaustive` as fallthrough-unreachable, so
// this is expected to spuriously fail to verify, reproducing the pre-fix bug for a
// different ExhaustivenessStatus variant.
@AlwaysVerify
fun <!VIPER_TEXT!>redundant<!>(p: Pair2): Int = when (p) {
    is Left -> p.v
    is <!DUPLICATE_BRANCH_CONDITION_IN_WHEN!>Left<!> -> p.v
    is Right -> p.v
}
