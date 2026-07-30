// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

// `predicate { }` as a sub-expression rather than the whole function body.
fun Interval.conjoined(): Boolean = predicate {
    lo <= hi
} && true

fun Interval.negated(): Boolean = !predicate {
    lo <= hi
}

// `predicate { }` bound to a property instead of naming a function.
val standalone: Boolean = predicate {
    true
}
