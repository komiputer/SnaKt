// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.<!MALFORMED_PREDICATE_DECLARATION, VERIFICATION_SKIPPED!>notBoolean<!>() {
    predicate {
        lo <= hi
    }
}

fun Interval.<!MALFORMED_PREDICATE_DECLARATION, VERIFICATION_SKIPPED!>extraStatements<!>(): Boolean {
    val bound = lo
    return predicate {
        bound <= hi
    }
}
