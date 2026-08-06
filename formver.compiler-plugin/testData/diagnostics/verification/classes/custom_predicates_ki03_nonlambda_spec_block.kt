// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.loopInvariants

// The same extraction path a predicate declaration goes through is shared by every specification
// block, and a stored function value reaches it from there too. The bar is the same: a diagnostic,
// not an internal compiler error.
class Interval(val lo: Int, val hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

val storedSpec: () -> Unit = {}

fun <!VERIFICATION_SKIPPED!>preconditionFromStoredBlock<!>(i: Interval): Int {
    <!MALFORMED_SPECIFICATION_BLOCK!>preconditions(storedSpec)<!>
    return i.lo
}

fun <!VERIFICATION_SKIPPED!>loopInvariantFromStoredBlock<!>(n: Int): Int {
    var i = 0
    while (i < n) {
        <!MALFORMED_SPECIFICATION_BLOCK!>loopInvariants(storedSpec)<!>
        i = i + 1
    }
    return i
}
