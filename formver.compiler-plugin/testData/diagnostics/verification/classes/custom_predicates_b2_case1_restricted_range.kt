// FULL_JDK
// RENDER_PREDICATES

// Community case: https://youtrack.jetbrains.com/issue/25-3765726
// Kotlin has no way to restrict Int to a subrange as a distinct type. A wrapper class plus a
// custom predicate lets a function require and preserve that restriction across construction.
//
// The `v` field must stay `var` for `Percent` to be a normal mutable wrapper, so no function body
// below reads `p.v` directly under a held predicate; every check goes through the `valid()`
// predicate access itself, which the plugin unfolds automatically.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions

class Percent(var v: Int)

fun Percent.valid(): Boolean = predicate {
    v >= 0 && v <= 100
}

fun requiresValidPercent(p: Percent) {
    preconditions {
        p.valid()
    }
}

fun makeValidPercent(x: Int): Percent {
    preconditions {
        x >= 0
        x <= 100
    }
    postconditions<Percent> { result ->
        result.valid()
    }
    return Percent(x)
}

fun useValidPercent() {
    requiresValidPercent(makeValidPercent(50))
}
