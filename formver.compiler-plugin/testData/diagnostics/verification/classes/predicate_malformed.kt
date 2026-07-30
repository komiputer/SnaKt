// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.notBoolean() {
    predicate {
        lo <= hi
    }
}

fun Interval.extraStatements(): Boolean {
    val bound = lo
    return predicate {
        bound <= hi
    }
}
