// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// Both properties are `val`, so a read embeds as a permission-free Viper function and the only thing
// standing between the precondition and the postcondition is the strength of the predicate itself.
class Box(val a: Int, val b: Int)

fun Box.aPositive(): Boolean = predicate {
    a > 0
}

// The predicate constrains `a`, not `b`. Returning `b` under a postcondition claiming the result is
// positive must be rejected by Viper: the precondition is too weak to establish it.
fun tooWeakForPostcondition(x: Box): Int {
    preconditions {
        x.aPositive()
    }
    postconditions<Int> {
        it > 0
    }
    return x.b
}

// The control: the same postcondition over the property the predicate does constrain. This must
// verify, which is what shows the case above fails for the intended reason rather than because the
// predicate carries no information at all.
fun strongEnoughForPostcondition(x: Box): Int {
    preconditions {
        x.aPositive()
    }
    postconditions<Int> {
        it > 0
    }
    return x.a
}

// The negative control for the case above: the same body and postcondition with the predicate
// precondition omitted. This must be rejected. If it verifies, the predicate contributed nothing and
// the control proves nothing about predicates.
fun postconditionWithoutPredicate(x: Box): Int {
    postconditions<Int> {
        it > 0
    }
    return x.a
}
