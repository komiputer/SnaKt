// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A predicate reference always requests full permission, so a callee that names the predicate in
// its precondition takes exclusive access and gives it back only by naming it again in its
// postcondition. These cases pin down that framing behaviour: what a caller must write to stay
// verifiable, and what it costs when the callee does not hand the access back.
class Interval(val lo: Int, val hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

// Returns the access it takes, so a caller may call it any number of times.
fun inspect(i: Interval) {
    preconditions {
        i.ordered()
    }
    postconditions<Unit> {
        i.ordered()
    }
}

// Takes the access and does not return it.
fun consume(i: Interval) {
    preconditions {
        i.ordered()
    }
}

@AlwaysVerify
fun twoInspections(i: Interval) {
    preconditions {
        i.ordered()
    }
    inspect(i)
    inspect(i)
}

@AlwaysVerify
fun inspectAfterConsume(i: Interval) {
    preconditions {
        i.ordered()
    }
    consume(i)
    <!VIPER_VERIFICATION_ERROR!>inspect(i)<!>
}
