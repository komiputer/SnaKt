// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.predicate

class Chain(val len: Int, val rest: Chain?) {
    fun descending(): Boolean = predicate {
        rest == null || (len > rest.len && rest.descending())
    }

    fun checkOutsideBlock(): Boolean {
        return descending()
    }
}
