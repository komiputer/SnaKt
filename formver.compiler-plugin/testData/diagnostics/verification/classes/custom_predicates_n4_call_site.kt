// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Box(val a: Int, val b: Int)

fun Box.aPositive(): Boolean = predicate {
    a > 0
}

fun requiresPositive(x: Box) {
    preconditions {
        x.aPositive()
    }
}

// The caller knows nothing about `x`, so it cannot establish the callee's predicate precondition.
// Viper must reject this call.
fun callWithoutPredicate(x: Box) {
    requiresPositive(x)
}

// The control: the caller forwards the same predicate, so the call site is justified and must verify.
fun callWithPredicate(x: Box) {
    preconditions {
        x.aPositive()
    }
    requiresPositive(x)
}
