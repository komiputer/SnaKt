// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// `predicate` takes a function value, so passing a stored one is legal Kotlin. The block
// extraction only handles a lambda literal and raises an internal error otherwise, which aborts
// the compilation instead of reporting a diagnostic. A user writing this must get told what is
// wrong with their program.
class Interval(val lo: Int, val hi: Int)

val storedBody: () -> Unit = {}

fun Interval.<!MALFORMED_PREDICATE_DECLARATION!>storedPredicateBody<!>(): Boolean = predicate(storedBody)

fun <!VERIFICATION_SKIPPED!>useStoredPredicateBody<!>(i: Interval) {
    preconditions {
        i.storedPredicateBody()
    }
}

// A function reference reaches the same extraction path by a different route.
fun <!VIPER_TEXT!>emptyBody<!>() {}

fun Interval.<!MALFORMED_PREDICATE_DECLARATION!>referencedPredicateBody<!>(): Boolean = predicate(::emptyBody)

fun <!VERIFICATION_SKIPPED!>useReferencedPredicateBody<!>(i: Interval) {
    preconditions {
        i.referencedPredicateBody()
    }
}
