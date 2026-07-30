// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun <!VIPER_TEXT!>accept<!>(b: Boolean): Int = if (b) 1 else 0

fun <!VERIFICATION_SKIPPED!>passAsArgument<!>(i: Interval): Int = accept(<!PREDICATE_OUTSIDE_SPECIFICATION!>i<!>.ordered())
