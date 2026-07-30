// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// A receiver the plugin has no class embedding for: an unconstrained type parameter.
fun <T> T.anything(): Boolean = predicate {
    true
}

fun String.nonEmpty(): Boolean = predicate {
    length > 0
}

// Each declaration needs a use site: a predicate declaration is not itself a verification target,
// so the receiver check is only reached once something refers to the predicate.
fun useAnything(x: Interval) {
    preconditions {
        x.anything()
    }
}

fun useNonEmpty(s: String) {
    preconditions {
        s.nonEmpty()
    }
}

class Interval(var lo: Int, var hi: Int)
