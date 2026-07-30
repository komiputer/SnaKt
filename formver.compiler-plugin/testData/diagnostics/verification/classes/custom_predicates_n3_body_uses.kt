// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun consume(flag: Boolean): Int = if (flag) 1 else 0

// A predicate named as an `if` condition in an ordinary function body.
fun asIfCondition(i: Interval): Int {
    if (i.ordered()) {
        return 1
    }
    return 0
}

// A predicate named as an argument to an ordinary function call.
fun asArgument(i: Interval): Int {
    return consume(i.ordered())
}

// A predicate named in a `while` condition, another ordinary-body position.
fun asLoopCondition(i: Interval): Int {
    var n = 0
    while (i.ordered()) {
        n = n + 1
        return n
    }
    return n
}
