// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// The emitted Viper predicate name is built from the class name and the function name alone, so
// two overloads on the same class map to the same name. The bodies here are contradictory: if the
// collision is resolved by keeping only one of them, a call to the other is verified against a
// body it never declared.
class Gauge(val reading: Int)

fun Gauge.<!MALFORMED_PREDICATE_DECLARATION!>level<!>(): Boolean = predicate {
    reading == 0
}

fun Gauge.<!MALFORMED_PREDICATE_DECLARATION!>level<!>(bound: Int): Boolean = predicate {
    reading == 1
}

// Callers of the two overloads. `reading == 0` and `reading == 1` cannot both hold, so any
// program in which both of these verify against the same predicate body is unsound.
@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>useLevelZero<!>(g: Gauge) {
    preconditions {
        g.level()
    }
}

@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>useLevelOne<!>(g: Gauge) {
    preconditions {
        g.level(1)
    }
}
