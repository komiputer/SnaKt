// FULL_JDK
// RENDER_PREDICATES

// Community case: https://youtrack.jetbrains.com/issue/25-6552305
// Kotlin's type system cannot express an ordering invariant across three properties of one
// object. A custom predicate can, and can be used as a precondition to prove a postcondition that
// depends on that ordering. All three properties are `val`, so the method body may read them
// directly without permission.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions

class Range(val lo: Int, val mid: Int, val hi: Int)

fun Range.wellFormed(): Boolean = predicate {
    lo <= mid && mid <= hi
}

fun span(r: Range): Int {
    preconditions {
        r.wellFormed()
    }
    postconditions<Int> { result ->
        result >= 0
    }
    return r.hi - r.lo
}
