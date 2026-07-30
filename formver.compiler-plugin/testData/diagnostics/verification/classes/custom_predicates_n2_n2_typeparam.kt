// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

fun <T> T.generic(): Boolean = predicate {
    true
}

fun useGeneric(x: Int) {
    preconditions {
        x.generic()
    }
}
