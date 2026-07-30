// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Node(val value: Int, val next: Node?)

fun Node?.maybeOrdered(): Boolean = predicate {
    true
}

fun <!VIPER_TEXT!>useMaybeOrdered<!>(n: Node?) {
    preconditions {
        n.maybeOrdered()
    }
}
