// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun <!VERIFICATION_SKIPPED!>checkWithIf<!>(i: Interval): Int {
    if (<!PREDICATE_OUTSIDE_SPECIFICATION!>i<!>.ordered()) {
        return 1
    }
    return 0
}
