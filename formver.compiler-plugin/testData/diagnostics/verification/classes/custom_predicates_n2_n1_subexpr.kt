// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.subExpressionPredicate(): Boolean = predicate {
    lo <= hi
} && true
