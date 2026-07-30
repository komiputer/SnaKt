// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Node(val value: Int, val next: Node?)

// A nullable class receiver. The API document does not state whether this is a class the plugin has
// an embedding for, so this case records the behaviour rather than asserting an expectation.
fun Node?.emptyOrPositive(): Boolean = predicate {
    this == null || value > 0
}

fun useNullableReceiver(n: Node?) {
    preconditions {
        n.emptyOrPositive()
    }
}
