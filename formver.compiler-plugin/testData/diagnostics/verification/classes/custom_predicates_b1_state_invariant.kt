// FULL_JDK
// RENDER_PREDICATES

// Community case: https://youtrack.jetbrains.com/issue/25-6552305
// A structural invariant over an object's whole state: the used region never exceeds the capacity,
// and a closed buffer is empty. Kotlin's type system cannot relate three properties, so the
// invariant lives in a predicate and every consumer requires it.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Buffer(val capacity: Int, val used: Int, val closed: Boolean)

fun Buffer.wellFormed(): Boolean = predicate {
    used >= 0 && used <= capacity && (!closed || used == 0)
}

fun flush(b: Buffer) {
    preconditions {
        b.wellFormed()
    }
}

fun flushTwice(b: Buffer) {
    preconditions {
        b.wellFormed()
    }
    flush(b)
    flush(b)
}
