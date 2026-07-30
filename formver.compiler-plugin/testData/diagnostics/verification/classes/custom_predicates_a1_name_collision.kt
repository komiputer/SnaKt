// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// Two unrelated classes each declaring `ordered()`. The short names collide, so `ShortNameResolver`
// must qualify both.
class Interval(val lo: Int, val hi: Int)

class Window(val lo: Int, val hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

fun Window.ordered(): Boolean = predicate {
    lo <= hi
}

fun <!VIPER_TEXT!>useBothOrdered<!>(i: Interval, w: Window) {
    preconditions {
        i.ordered()
        w.ordered()
    }
}
