// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.verify

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun <!VIPER_TEXT!>useOrdered<!>(i: Interval) {
    preconditions {
        i.ordered()
    }
    verify(i.lo <= i.hi)
}

class Node(var value: Int, val next: Node?)

fun Node.sorted(): Boolean = predicate {
    next == null || (value <= next.value && next.sorted())
}

fun <!VIPER_TEXT!>useSorted<!>(n: Node) {
    preconditions {
        n.sorted()
    }
}
