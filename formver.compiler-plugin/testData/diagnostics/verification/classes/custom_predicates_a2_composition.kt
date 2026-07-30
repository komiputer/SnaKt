// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A12: several predicates declared on one class, one naming another (non-recursive
// composition).
class Box(val a: Int, val b: Int, val c: Int) {
    fun aLeB(): Boolean = predicate {
        a <= b
    }

    fun bLeC(): Boolean = predicate {
        b <= c
    }

    fun sorted(): Boolean = predicate {
        aLeB() && bLeC()
    }
}

@AlwaysVerify
fun <!VIPER_TEXT!>useSortedBox<!>(x: Box) {
    preconditions {
        x.sorted()
    }
}

// A12: mutual recursion between two predicates declared on two different classes. Each
// predicate is defined in terms of the other before the other's declaration is seen in
// source order, checking that forward reference between predicate declarations resolves.
class NodeA(val value: Int, val partner: NodeB?)

class NodeB(val value: Int, val partner: NodeA?)

fun NodeA.aInv(): Boolean = predicate {
    partner == null || (value <= partner.value && partner.bInv())
}

fun NodeB.bInv(): Boolean = predicate {
    partner == null || (value <= partner.value && partner.aInv())
}

@AlwaysVerify
fun <!VIPER_TEXT!>useMutualRecursion<!>(a: NodeA) {
    preconditions {
        a.aInv()
    }
}
