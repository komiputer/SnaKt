// FULL_JDK
// RENDER_PREDICATES

// THIS CASE IS EXPECTED TO FAIL, and the failure is the point: it pins a limit, not a bug.
//
// A predicate access embeds with full permission, so forwarding it to a consumer exhales all of it.
// The caller does not get it back, and there is no way to re-establish it: the plugin constructs no
// `Stmt.Fold` anywhere outside `viper/ast/`. So a caller holding `b.wellFormed()` can forward it
// exactly once. The first call below verifies; the second cannot, and carries the marker.
//
// IF FOLD IS EVER IMPLEMENTED THIS CASE INVERTS. The second call should then verify and this golden
// will break. That break is correct and means the limit has been lifted — do not repair it by
// re-pinning the failure.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Store(val capacity: Int, val used: Int)

fun Store.wellFormed(): Boolean = predicate {
    used >= 0 && used <= capacity
}

fun drain(s: Store) {
    preconditions {
        s.wellFormed()
    }
}

fun drainTwice(s: Store) {
    preconditions {
        s.wellFormed()
    }
    drain(s)
    drain(s)
}
