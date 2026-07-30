// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// A predicate is scoped to the class that declares it. `bounded` belongs to `Shape`; no separate
// `Box` predicate is emitted, and a `Box` subject reaches the `Shape` predicate unchanged.
open class Shape(val size: Int) {
    fun bounded(): Boolean = predicate {
        0 <= size
    }
}

class Box(val depth: Int) : Shape(2)

fun <!VIPER_TEXT!>useShapePredicate<!>(s: Shape) {
    preconditions {
        s.bounded()
    }
}

fun <!VIPER_TEXT!>useBoxThroughSuperclassPredicate<!>(b: Box) {
    preconditions {
        b.bounded()
    }
}
