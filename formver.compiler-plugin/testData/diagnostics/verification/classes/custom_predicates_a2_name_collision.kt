// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A3: two unrelated classes each declare a predicate with the same simple name. Both must
// emit correctly; ShortNameResolver is expected to qualify at least one of them since the
// bare name collides.
class IntervalX(val lo: Int, val hi: Int)

fun IntervalX.ordered(): Boolean = predicate {
    lo <= hi
}

class PairX(val first: Int, val second: Int)

fun PairX.ordered(): Boolean = predicate {
    first <= second
}

@AlwaysVerify
fun useIntervalXOrdered(i: IntervalX) {
    preconditions {
        i.ordered()
    }
}

@AlwaysVerify
fun usePairXOrdered(p: PairX) {
    preconditions {
        p.ordered()
    }
}

// Both predicates used together in the same function, to force both qualified names to
// appear in one generated program.
@AlwaysVerify
fun useBothOrdered(i: IntervalX, p: PairX) {
    preconditions {
        i.ordered()
        p.ordered()
    }
}
