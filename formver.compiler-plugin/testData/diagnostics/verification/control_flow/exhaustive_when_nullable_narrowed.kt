// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Tri
object T1 : Tri
object T2 : Tri

// A `when` with a nullable sealed subject, guarded by a prior null-check that
// narrows the subject to non-null (KT-7301055 shape), exhaustive over the
// non-null cases with no `else`. This is the false-negative direction: it is safe
// either way, since a `when` FIR fails to mark `ProperlyExhaustive` just falls back
// to the old (safe) UnitLit path. Whether SnaKt proves this at all is recorded in
// the report, not treated as a defect if it fails to verify.
@AlwaysVerify
fun <!VIPER_TEXT!>viaNullCheck<!>(t: Tri?): Int {
    return if (t == null) {
        0
    } else {
        when (t) {
            is T1 -> 1
            is T2 -> 2
        }
    }
}
