// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Node
class Leaf(val value: Int) : Node
class Branch(val weight: Int) : Node

// `Leaf` and `Branch` already cover `Node`, so the trailing `else` is redundant. FIR reports this
// `when` as `RedundantlyExhaustive`, a status the plugin does not treat as fallthrough-unreachable.
// It verifies anyway: a redundant `when` always has a syntactic `else`, so the fallthrough the
// plugin would have to mark unreachable does not exist.
@AlwaysVerify
fun <!VIPER_TEXT!>weightWithElse<!>(n: Node): Int = when (n) {
    is Leaf -> n.value
    is Branch -> n.weight
    <!REDUNDANT_ELSE_IN_WHEN!>else<!> -> 0
}

// A duplicated condition is not what makes a `when` redundantly exhaustive: with no `else` this is
// still `ProperlyExhaustive`, so the missing fallthrough is marked unreachable and it verifies.
@AlwaysVerify
fun <!VIPER_TEXT!>weightOf<!>(n: Node): Int = when (n) {
    is Leaf -> n.value
    is <!DUPLICATE_BRANCH_CONDITION_IN_WHEN!>Leaf<!> -> 0
    is Branch -> n.weight
}
