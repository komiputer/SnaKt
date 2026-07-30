// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// Adversarial: a predicate declared `open` and overridden in a subclass. Two declarations of the
// same name in one hierarchy, each scoped to its own declaring class.
open class Container(val count: Int) {
    open fun filled(): Boolean = predicate {
        0 < count
    }
}

class Crate(val extra: Int) : Container(1) {
    override fun filled(): Boolean = predicate {
        0 < count && 0 <= extra
    }
}

fun <!VIPER_TEXT!>useContainerPredicate<!>(c: Container) {
    preconditions {
        c.filled()
    }
}

fun <!VIPER_TEXT!>useCratePredicate<!>(c: Crate) {
    preconditions {
        c.filled()
    }
}
