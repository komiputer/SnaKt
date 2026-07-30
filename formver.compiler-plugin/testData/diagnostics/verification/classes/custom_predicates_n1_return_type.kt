// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

// A `predicate { }` block in a function whose return type is neither `Boolean` nor `Unit`.
fun Interval.asInt(): Int {
    predicate {
        lo <= hi
    }
    return lo
}

fun Interval.asString(): String {
    predicate {
        lo <= hi
    }
    return "ordered"
}
