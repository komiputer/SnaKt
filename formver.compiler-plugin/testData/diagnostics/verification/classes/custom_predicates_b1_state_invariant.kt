// FULL_JDK
// RENDER_PREDICATES

// Two community cases, merged: they ask for the same thing about one object's state.
//   https://youtrack.jetbrains.com/issue/25-1351430 — an invariant known internally but
//     inexpressible in the type: `payload` is guaranteed non-null once the buffer holds anything,
//     yet its public type has to stay `String?`.
//   https://youtrack.jetbrains.com/issue/25-6552305 — a structural invariant over the whole state:
//     the used region never exceeds the capacity.
// One predicate states both, which is why these do not need two programs.
// Every property is `val`: the plugin has no `!!`, so the non-null guarantee is reached off an
// immutable property rather than a dereference.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Buffer(val capacity: Int, val used: Int, val payload: String?)

fun Buffer.wellFormed(): Boolean = predicate {
    used >= 0 && used <= capacity && (used == 0 || payload != null)
}

fun flush(b: Buffer) {
    preconditions {
        b.wellFormed()
    }
}

// A caller that holds the invariant forwards it to the consumer. This verifies.
fun flushOnce(b: Buffer) {
    preconditions {
        b.wellFormed()
    }
    flush(b)
}

// Negative control for `flushOnce`: the same caller without the precondition. It cannot supply
// `acc(wellFormed(b))` at the call site, so it must be rejected.
fun flushUnchecked(b: Buffer) {
    flush(b)
}
