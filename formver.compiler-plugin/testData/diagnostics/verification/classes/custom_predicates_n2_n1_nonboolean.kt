// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.<!MALFORMED_PREDICATE_DECLARATION, VERIFICATION_SKIPPED!>badReturnType<!>(): Int {
    predicate {
        lo <= hi
    }
    return lo
}
