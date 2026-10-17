// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// Set.contains is pure and leaves the receiver's size unchanged. Indexing an
// array sized from that (unchanged) size is only provably safe if the
// postcondition holds.

@AlwaysVerify
fun <!VIPER_TEXT!>contains_preserves_size<!>(s: Set<Int>, x: Int): Int {
    val before = s.size
    s.contains(x)
    val arr = IntArray(s.size - before + 1)
    return arr[0]
}
