// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun callInLambda(i: Interval): Int {
    val check = { i.ordered() }
    return if (check()) 1 else 0
}
