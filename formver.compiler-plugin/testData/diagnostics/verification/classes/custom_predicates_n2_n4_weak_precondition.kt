// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions

class Interval(val lo: Int, val hi: Int)

fun Interval.nonNegativeLo(): Boolean = predicate {
    lo >= 0
}

fun weakPrecondition(i: Interval): Int {
    preconditions {
        i.nonNegativeLo()
    }
    postconditions<Int> {
        it > 0
    }
    return i.lo
}
