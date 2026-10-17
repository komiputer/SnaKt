// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// MutableSet.add carries a postcondition that the receiver's size grows by
// exactly one when the element was absent (add returns true), and stays the
// same when it was already present (add returns false). Indexing an array
// sized from that growth is only provably safe if the postcondition holds
// and is gated on the returned value.

@AlwaysVerify
fun <!VIPER_TEXT!>add_then_index_growth<!>(s: MutableSet<Int>, x: Int): Int {
    val before = s.size
    val added = s.add(x)
    val arr = IntArray(s.size - before)
    return if (added) arr[0] else 0
}
