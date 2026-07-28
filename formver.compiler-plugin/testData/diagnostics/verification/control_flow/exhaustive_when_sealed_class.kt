// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed class Node(val tag: Int)
class Leaf(val payload: Int) : Node(0)
class Branch(val left: Leaf) : Node(1)

// Same shape as the sealed-interface baseline, but over a sealed `class` hierarchy with a common
// constructor. The `when` is total, so the missing fallthrough is unreachable and the function
// verifies as always returning an Int.
@AlwaysVerify
fun <!VIPER_TEXT!>weight<!>(n: Node): Int = when (n) {
    is Leaf -> n.payload
    is Branch -> n.left.payload
}
