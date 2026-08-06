// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.loopInvariants
import org.jetbrains.kotlin.formver.plugin.verify
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

class Bound(val lo: Int, val hi: Int)

fun Bound.ordered(): Boolean = predicate {
    lo <= hi
}

// A11 positive: a predicate access as the entire postcondition body.
@AlwaysVerify
fun makeOrdered(a: Int, b: Int): Bound {
    postconditions<Bound> { result ->
        result.ordered()
    }
    return if (a <= b) Bound(a, b) else Bound(b, a)
}

// A11 adversarial: the postcondition is a predicate access, but the returned value violates it.
@AlwaysVerify
fun makeUnordered(a: Int, b: Int): Bound {
    postconditions<Bound> { result ->
        result.ordered()
    }
    return Bound(b, a)
}

// A11 positive: a predicate access inside loopInvariants, held on a var whose value is a
// freshly-constructed object each iteration (never a field mutation, so the val-only
// limitation on predicate reads does not apply to the loop variable itself).
@AlwaysVerify
fun loopWithPredicateInvariant(n: Int) {
    preconditions {
        n >= 0
    }
    var b = Bound(0, n)
    var i = 0
    while (i < n) {
        loopInvariants {
            b.ordered()
            i <= n
        }
        i = i + 1
    }
}

// A11 adversarial: the loop invariant asserts a predicate access that the loop body breaks
// on the very first iteration by rebinding the var to an object that violates it.
@AlwaysVerify
fun loopBreaksPredicateInvariant(n: Int) {
    preconditions {
        n >= 1
    }
    var b = Bound(0, n)
    var i = 0
    while (i < n) {
        loopInvariants {
            b.ordered()
            i <= n
        }
        b = Bound(n, 0)
        i = i + 1
    }
}
