// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// The class embedding a predicate is built on does not model generic parameters, and nothing says
// what a predicate on a generic class means. The two instantiations below share one class
// embedding, so they share one predicate; this file records whether that is expressible at all,
// and if it is, whether the subject's type argument survives into the emitted program.
class Cell<T>(val item: T, val tag: Int)

fun <T> Cell<T>.tagged(): Boolean = predicate {
    tag > 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>useIntCell<!>(c: Cell<Int>) {
    preconditions {
        c.tagged()
    }
}

@AlwaysVerify
fun <!VIPER_TEXT!>useStringCell<!>(c: Cell<String>) {
    preconditions {
        c.tagged()
    }
}

// A predicate whose body constrains the type parameter itself, where erasing the parameter is not
// obviously harmless.
class Pair2<A>(val left: A, val right: A)

fun <A> Pair2<A>.sameIdentity(): Boolean = predicate {
    left === right
}

@AlwaysVerify
fun <!VIPER_TEXT!>useSameIdentity<!>(p: Pair2<Cell<Int>>) {
    preconditions {
        p.sameIdentity()
    }
}
