// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

val alwaysOrdered: Boolean = predicate {
    true
}
