// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

sealed interface Node
sealed interface Inner : Node
class Left(val v: Int) : Inner
class Right(val v: Int) : Inner
class Outer(val v: Int) : Node

sealed interface L1
sealed interface L2 : L1
sealed interface L3 : L2
class Leaf3(val v: Int) : L3
class Leaf2(val v: Int) : L2
class Leaf1(val v: Int) : L1

// A `when` inside a branch that has already narrowed the subject to a sealed sub-hierarchy. Both
// the outer and the inner `when` are exhaustive with no `else`, so both rely on the fallthrough
// being impossible.
@AlwaysVerify
fun <!VIPER_TEXT!>nested<!>(n: Node): Int = when (n) {
    is Inner -> when (n) {
        is Left -> n.v
        is Right -> n.v
    }
    is Outer -> n.v
}

// Three levels of sealed interface, covered by a mix of intermediate and leaf types.
@AlwaysVerify
fun <!VIPER_TEXT!>deep<!>(x: L1): Int = when (x) {
    is L3 -> when (x) {
        is Leaf3 -> x.v
    }
    is Leaf2 -> x.v
    is Leaf1 -> x.v
}

// Nesting must not weaken what is checked inside the branches: `Right` contributes a negative
// value, so the postcondition is false and must be rejected.
@AlwaysVerify
fun <!VIPER_TEXT!>nestedBranchBodiesChecked<!>(n: Node): Int {
    val r = when (n) {
        is Inner -> when (n) {
            is Left -> n.v
            is Right -> -n.v
        }
        is Outer -> n.v
    }
    verify(<!VIPER_VERIFICATION_ERROR!>r >= 0<!>)
    return r
}
