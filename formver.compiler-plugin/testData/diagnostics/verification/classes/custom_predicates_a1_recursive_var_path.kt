// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

// A recursive predicate requires `val` throughout its read path. The link must already be a `val`
// because the plugin has no `!!` and relies on smart-casting, and a `val` read embeds as a
// permission-free Viper function. A `var` read needs permission that inside a predicate body is
// available only within the recursive occurrence of the predicate, so this declaration does not
// yield verifying Viper. This case pins that documented limit.
class VarNode(var value: Int, val next: VarNode?)

fun VarNode.sortedVar(): Boolean = predicate {
    next == null || (value <= next.value && next.sortedVar())
}

fun <!VIPER_TEXT!>useSortedVar<!>(n: VarNode) {
    preconditions {
        n.sortedVar()
    }
}

// The same shape with `val` throughout verifies, for contrast.
class ValNode(val value: Int, val next: ValNode?)

fun ValNode.sortedVal(): Boolean = predicate {
    next == null || (value <= next.value && next.sortedVal())
}

fun <!VIPER_TEXT!>useSortedVal<!>(n: ValNode) {
    preconditions {
        n.sortedVal()
    }
}
