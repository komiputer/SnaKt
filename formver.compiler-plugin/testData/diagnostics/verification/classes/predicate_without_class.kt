// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

fun noReceiver(): Boolean = predicate {
    true
}

fun Int.positive(): Boolean = predicate {
    this > 0
}

// The diagnostic is reached through the use site: a predicate declaration is not itself a
// verification target, so it is only embedded once something refers to it.
fun useNoReceiver() {
    preconditions {
        noReceiver()
    }
}

fun usePositive(n: Int) {
    preconditions {
        n.positive()
    }
}
