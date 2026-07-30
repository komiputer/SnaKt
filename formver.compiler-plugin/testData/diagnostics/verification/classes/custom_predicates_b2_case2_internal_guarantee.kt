// FULL_JDK
// RENDER_PREDICATES

// Community case: https://youtrack.jetbrains.com/issue/25-1351430
// A class can know internally that a nullable property is non-null whenever another property
// holds a given value, but a public `T?` type cannot state that guarantee. A custom predicate can
// relate the two properties directly. Both properties are `val`, so reading `cache` needs no
// permission and no `!!`; Kotlin's ordinary smart-cast on the `val` does the rest.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions

class LazyBox(val computed: Boolean, val cache: Int?)

fun LazyBox.cacheValid(): Boolean = predicate {
    !computed || cache != null
}

fun readCache(b: LazyBox): Int {
    preconditions {
        b.computed
        b.cacheValid()
    }
    postconditions<Int> { result ->
        result >= 0
    }
    return if (b.cache != null) b.cache else 0
}
