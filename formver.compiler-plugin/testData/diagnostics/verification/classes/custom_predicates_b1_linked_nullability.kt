// FULL_JDK
// RENDER_PREDICATES

// Community case: https://stackoverflow.com/questions/53738456/kotlin-contracts-link-not-null-of-two-properties
// A Kotlin contract can only talk about one value at a time, so there is no way to say "a is null
// exactly when b is null" about two properties of the same receiver. A custom predicate relates
// them directly, and a consumer requires the relation as a precondition.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Pair2(val a: String?, val b: String?)

fun Pair2.linked(): Boolean = predicate {
    (a == null) == (b == null)
}

fun consumeBoth(p: Pair2) {
    preconditions {
        p.linked()
    }
}

// A caller that holds the relation forwards it to the consumer. This verifies.
fun consumeBothOnce(p: Pair2) {
    preconditions {
        p.linked()
    }
    consumeBoth(p)
}

// Negative control for `consumeBothOnce`: the same caller without the precondition. It cannot supply
// `acc(linked(p))` at the call site, so it must be rejected.
fun consumeBothUnchecked(p: Pair2) {
    consumeBoth(p)
}
