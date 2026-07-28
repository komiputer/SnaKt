// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Outer
class OuterA(val inner: Inner) : Outer
class OuterB(val n: Int) : Outer

sealed interface Inner
object InnerX : Inner
object InnerY : Inner

// A nested exhaustive `when` (over Inner) inside a branch of an outer exhaustive
// `when` (over Outer) must verify correctly, and the inner unreachable-fallthrough
// marking must not leak into or corrupt the outer one.
@AlwaysVerify
fun <!VIPER_TEXT!>nested<!>(o: Outer): Int = when (o) {
    is OuterA -> when (o.inner) {
        is InnerX -> 1
        is InnerY -> 2
    }
    is OuterB -> o.n
}
