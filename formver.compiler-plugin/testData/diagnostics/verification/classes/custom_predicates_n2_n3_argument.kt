// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun accept(b: Boolean): Int = if (b) 1 else 0

fun passAsArgument(i: Interval): Int = accept(i.ordered())
