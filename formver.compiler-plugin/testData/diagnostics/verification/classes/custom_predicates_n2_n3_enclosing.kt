// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Chain(val len: Int, val rest: Chain?) {
    fun descending(): Boolean = predicate {
        rest == null || (len > rest.len && rest.descending())
    }

    fun <!VERIFICATION_SKIPPED!>checkOutsideBlock<!>(): Boolean {
        return <!PREDICATE_OUTSIDE_SPECIFICATION!>descending()<!>
    }
}
