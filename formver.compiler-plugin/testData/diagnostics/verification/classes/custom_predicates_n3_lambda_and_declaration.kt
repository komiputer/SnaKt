// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

// A predicate named inside a lambda in an ordinary function body. The lambda is not a specification
// block, so the call is still outside a specification.
fun inLambda(i: Interval): Boolean {
    val check = { i.ordered() }
    return check()
}

// A predicate named in the enclosing function of another predicate declaration, but outside its
// `predicate { }` block. The statement outside the block also makes the declaration malformed, so
// this case records which diagnostics fire together.
fun Interval.alsoOrdered(): Boolean {
    val known = ordered()
    return predicate {
        known
    }
}
