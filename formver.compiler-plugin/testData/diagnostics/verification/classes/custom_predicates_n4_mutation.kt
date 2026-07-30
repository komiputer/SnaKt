// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun requiresOrdered(i: Interval) {
    preconditions {
        i.ordered()
    }
}

// The subject is mutated so its predicate no longer holds, then passed where the predicate is
// required. Viper must reject the call. Neither function here reads a `var` field, only writes it, so
// the general `var`-field-read limitation is not in play.
fun mutateThenPass(i: Interval) {
    i.lo = 10
    i.hi = 0
    requiresOrdered(i)
}

// The control: the same shape with a mutation that leaves the predicate holding. This must verify,
// which is what distinguishes a rejection caused by the broken predicate from one caused by missing
// permission to write the fields at all.
fun mutateAndKeepOrdered(i: Interval) {
    i.lo = 0
    i.hi = 10
    requiresOrdered(i)
}
