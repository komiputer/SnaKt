// FULL_JDK
// RENDER_PREDICATES

// Community case (Method B, priority): https://stackoverflow.com/questions/53738456/kotlin-contracts-link-not-null-of-two-properties
// Kotlin contracts cannot state that two properties of one receiver are null together or non-null
// together. A custom predicate can, and can be verified across construction.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.postconditions

class Pair2(val a: Int?, val b: Int?)

fun Pair2.linked(): Boolean = predicate {
    (a == null) == (b == null)
}

fun makeLinkedPair(x: Int?): Pair2 {
    postconditions<Pair2> { result ->
        result.linked()
    }
    return Pair2(x, x)
}
