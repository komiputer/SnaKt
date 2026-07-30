// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// Three predicates on one class, the third naming the first two on its own receiver.
class Segment(val start: Int, val end: Int)

fun Segment.ordered(): Boolean = predicate {
    start <= end
}

fun Segment.nonNegative(): Boolean = predicate {
    0 <= start
}

fun Segment.sane(): Boolean = predicate {
    ordered() && nonNegative()
}

fun <!VIPER_TEXT!>useSeveralPredicates<!>(s: Segment) {
    preconditions {
        s.ordered()
        s.nonNegative()
    }
}

fun <!VIPER_TEXT!>useComposedPredicate<!>(s: Segment) {
    preconditions {
        s.sane()
    }
    postconditions<Unit> {
        s.sane()
    }
}

// Negative control for the composed case, and a same-arity swap in one: the precondition supplies
// `ordered(s)` while the postcondition claims the different predicate `sane(s)`. Nothing establishes
// `sane(s)`, so it must be rejected. If it verifies, a predicate access is being assumed rather than
// checked and the positive case above proves nothing.
fun <!VIPER_TEXT!>swapComposedForOrdered<!>(s: Segment) {
    preconditions {
        s.ordered()
    }
    postconditions<Unit> {
        s.sane()
    }
}

// Mutual recursion between two predicates, with a forward reference: `evenChain` names `oddChain`
// before it is declared.
class Alt(val v: Int, val next: Alt?)

fun Alt.evenChain(): Boolean = predicate {
    next == null || next.oddChain()
}

fun Alt.oddChain(): Boolean = predicate {
    next == null || next.evenChain()
}

fun <!VIPER_TEXT!>useMutualRecursion<!>(a: Alt) {
    preconditions {
        a.evenChain()
    }
    postconditions<Unit> {
        a.evenChain()
    }
}

// Negative control for the mutual-recursion case: nothing on entry establishes `evenChain(a)`, so the
// postcondition claiming it must be rejected.
fun <!VIPER_TEXT!>claimEvenChainUnbacked<!>(a: Alt) {
    postconditions<Unit> {
        a.evenChain()
    }
}

// Negative control that is also a same-arity swap: `oddChain` is held on entry and `evenChain` is
// claimed on exit. The two are distinct predicates over one `Alt`, so this must be rejected.
fun <!VIPER_TEXT!>swapOddChainForEvenChain<!>(a: Alt) {
    preconditions {
        a.oddChain()
    }
    postconditions<Unit> {
        a.evenChain()
    }
}
