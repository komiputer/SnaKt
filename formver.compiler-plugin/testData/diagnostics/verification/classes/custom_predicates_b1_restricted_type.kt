// FULL_JDK
// RENDER_PREDICATES

// Community case: https://youtrack.jetbrains.com/issue/25-3765726 (restricted types)
// Kotlin has no way to say "an Int between 0 and 100" in a type, so a wrapper class carries the
// range as an undocumented convention. A custom predicate states the range and a consumer requires
// it, so the range becomes a checked precondition rather than a comment.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Percent(var v: Int)

fun Percent.valid(): Boolean = predicate {
    v >= 0 && v <= 100
}

// The range restriction stands in for a type refinement: reaching `applyDiscount` requires holding
// `valid(p)`.
fun applyDiscount(p: Percent) {
    preconditions {
        p.valid()
    }
}

// A caller that holds the restriction forwards it to the consumer, twice, so the predicate access is
// not consumed by the first call.
fun discountTwice(p: Percent) {
    preconditions {
        p.valid()
    }
    applyDiscount(p)
    applyDiscount(p)
}

// Negative control for `discountTwice`: the same caller without the precondition. It cannot supply
// `acc(valid(p))` at the call site, so it must be rejected. Without this the positive case would
// pass whether or not the predicate access is checked at all.
fun discountUnchecked(p: Percent) {
    applyDiscount(p)
}
