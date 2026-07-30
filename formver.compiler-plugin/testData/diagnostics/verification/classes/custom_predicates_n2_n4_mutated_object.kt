// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun needsOrdered(i: Interval) {
    preconditions {
        i.ordered()
    }
}

fun mutateThenCall(i: Interval) {
    preconditions {
        i.ordered()
    }
    i.hi = i.lo - 1
    needsOrdered(i)
}
