// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

fun <T> T.<!PREDICATE_WITHOUT_CLASS!>generic<!>(): Boolean = predicate {
    true
}

fun <!VERIFICATION_SKIPPED!>useGeneric<!>(x: Int) {
    preconditions {
        x.generic()
    }
}
