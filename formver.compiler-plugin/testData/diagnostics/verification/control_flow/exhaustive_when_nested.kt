// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Inner
class IA(val v: Int) : Inner
class IB(val v: Int) : Inner

sealed interface Outer
class OA(val v: Int) : Outer
class OB(val inner: Inner) : Outer

// An exhaustive `when` nested inside a branch of another exhaustive `when`. Both fallthroughs are
// unreachable; the inner marking must not leak into or corrupt the outer one, so the function
// verifies as always returning an Int.
@AlwaysVerify
fun <!VIPER_TEXT!>nested<!>(o: Outer): Int = when (o) {
    is OA -> o.v
    is OB -> {
        val i = o.inner
        when (i) {
            is IA -> i.v
            is IB -> i.v
        }
    }
}
