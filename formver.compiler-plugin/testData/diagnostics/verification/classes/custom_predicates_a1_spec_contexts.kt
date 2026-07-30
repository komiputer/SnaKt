// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.loopInvariants
import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// Every property a predicate body reads is `val`, so the reads embed as permission-free Viper
// functions rather than field accesses.
class Interval(val lo: Int, val hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

// A second predicate over the same class, used as a same-arity swap below.
fun Interval.tight(): Boolean = predicate {
    lo == hi
}

// A predicate access held on entry and restated on exit: the method touches nothing, so the
// permission is still held at the end.
fun <!VIPER_TEXT!>forwardOrdered<!>(i: Interval) {
    preconditions {
        i.ordered()
    }
    postconditions<Unit> {
        i.ordered()
    }
}

// The same predicate access carried across a loop as a loop invariant.
fun <!VIPER_TEXT!>carryOrderedThroughLoop<!>(i: Interval, n: Int) {
    preconditions {
        i.ordered()
    }
    postconditions<Unit> {
        i.ordered()
    }
    var k = 0
    while (k < n) {
        loopInvariants {
            i.ordered()
        }
        k = k + 1
    }
}

// Adversarial: a postcondition claiming a predicate access that was never required on entry and is
// never folded in the body. Nothing establishes it, so it must not be provable.
fun <!VIPER_TEXT!>claimOrderedUnbacked<!>(i: Interval) {
    postconditions<Unit> {
        i.ordered()
    }
}

// Same-arity swap: `ordered(i)` is held on entry and the distinct predicate `tight(i)` is claimed on
// exit. It must be rejected; verifying would mean a predicate access is assumed rather than checked.
fun <!VIPER_TEXT!>swapOrderedForTight<!>(i: Interval) {
    preconditions {
        i.ordered()
    }
    postconditions<Unit> {
        i.tight()
    }
}

// Adversarial: a loop invariant claiming a predicate access absent from the precondition. It cannot
// hold on the first iteration.
fun <!VIPER_TEXT!>claimOrderedInLoop<!>(i: Interval, n: Int) {
    var k = 0
    while (k < n) {
        loopInvariants {
            i.ordered()
        }
        k = k + 1
    }
}
