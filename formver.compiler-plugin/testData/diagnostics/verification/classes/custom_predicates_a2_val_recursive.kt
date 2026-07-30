// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A13, confirmation: a recursive predicate requires `val` throughout the property it reads,
// not just at the recursive link. Both `value` and `next` are `val` here; a `var` in either
// position would need permission that is only available inside the recursive occurrence of
// the predicate and would not verify. This is a documented expressiveness limit
// (SPECIFICATIONS.md as of e1cd7c1c), not a bug, and this case is not meant to challenge it,
// only to confirm it holds through a three-link chain.
class Link(val value: Int, val next: Link?)

fun Link.nonDecreasing(): Boolean = predicate {
    next == null || (value <= next.value && next.nonDecreasing())
}

@AlwaysVerify
fun <!VIPER_TEXT!>useNonDecreasing<!>(l: Link) {
    preconditions {
        l.nonDecreasing()
    }
}

@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>buildAndUse<!>(): Boolean {
    val tail = Link(3, null)
    val mid = Link(2, tail)
    val head = Link(1, mid)
    return <!PREDICATE_OUTSIDE_SPECIFICATION!>head<!>.nonDecreasing()
}
