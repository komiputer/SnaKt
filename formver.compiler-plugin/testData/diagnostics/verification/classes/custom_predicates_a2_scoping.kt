// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A10: a predicate is scoped to its declaring class. A subclass that does not redeclare the
// predicate still resolves the call to the base class's predicate through ordinary Kotlin
// member-function inheritance; the plugin must not mint a second, subclass-scoped predicate
// for the inherited call.
class Base(val x: Int) {
    fun nonneg(): Boolean = predicate {
        x >= 0
    }
}

class Derived(x: Int) : Base(x)

@AlwaysVerify
fun useOnDerived(d: Derived) {
    preconditions {
        d.nonneg()
    }
}

@AlwaysVerify
fun useOnBase(b: Base) {
    preconditions {
        b.nonneg()
    }
}
