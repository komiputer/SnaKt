// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// MutableSet.remove carries a postcondition that the receiver's size shrinks
// by exactly one when the element was present (remove returns true), and
// stays the same when it wasn't (remove returns false).

@AlwaysVerify
fun <!VIPER_TEXT!>remove_then_index_shrink<!>(s: MutableSet<Int>, x: Int): Int {
    val before = s.size
    val removed = s.remove(x)
    val arr = IntArray(before - s.size)
    return if (removed) arr[0] else 0
}
