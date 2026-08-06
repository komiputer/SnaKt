// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.verify

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

// The precondition holds `ordered(i)`, but reading the `var` fields `lo` and `hi` in a method body
// yields fresh unconstrained values regardless of the permissions held, so the assertion cannot be
// discharged. This is the general `var`-field-read limitation, not specific to custom predicates.
fun <!VIPER_TEXT!>useOrdered<!>(i: Interval) {
    preconditions {
        i.ordered()
    }
    verify(<!VIPER_VERIFICATION_ERROR!>i.lo <= i.hi<!>)
}

// Both properties are `val`: a recursive predicate reads them through its own link, and a `val`
// embeds as a permission-free Viper function, whereas a `var` field read would need permission that
// is held only inside the recursive occurrence of the predicate.
class Node(val value: Int, val next: Node?)

fun Node.sorted(): Boolean = predicate {
    next == null || (value <= next.value && next.sorted())
}

fun <!VIPER_TEXT!>useSorted<!>(n: Node) {
    preconditions {
        n.sorted()
    }
}

// A predicate declared as a member function rather than an extension, so its subject is the dispatch
// receiver. Recursive, to exercise the dispatch-receiver name through a self-reference.
class Chain(val len: Int, val rest: Chain?) {
    fun descending(): Boolean = predicate {
        rest == null || (len > rest.len && rest.descending())
    }
}

fun <!VIPER_TEXT!>useDescending<!>(c: Chain) {
    preconditions {
        c.descending()
    }
}
