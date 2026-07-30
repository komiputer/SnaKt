// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Interval(val lo: Int, val hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun needsOrdered(i: Interval) {
    preconditions {
        i.ordered()
    }
}

fun callWithoutEstablishing(i: Interval) {
    needsOrdered(i)
}
