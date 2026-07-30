// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Interval(val lo: Int, val hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun <!VIPER_TEXT!>needsOrdered<!>(i: Interval) {
    preconditions {
        i.ordered()
    }
}

fun <!VIPER_TEXT!>callWithoutEstablishing<!>(i: Interval) {
    <!VIPER_VERIFICATION_ERROR!>needsOrdered(i)<!>
}
