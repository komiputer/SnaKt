// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.loopInvariants
import org.jetbrains.kotlin.formver.plugin.postconditions

// #252: No indexed-element write (a[i] = v) for List/MutableList.
// MutableList.set has no StdLibPrecondition/StdLibPostcondition and no class-scoped embedding.
// The desugared FirUnitExpression from `l[i] = v` hits an unimplemented handler.
// The INTERNAL_ERROR lands on the indexed-assignment expression; only the FIRST write
// in each function is marked (subsequent ones are never reached after the crash).

// Simplest single-element write.
fun singleWrite(l: MutableList<Int>) {
    <!INTERNAL_ERROR!>l[0] = 42<!>
}

// Write to a variable index.
fun writeAtIndex(l: MutableList<Int>, i: Int) {
    <!INTERNAL_ERROR!>l[i] = 0<!>
}

// Swap of two elements — the canonical in-place blocked operation.
// The first write crashes; the second write (l[j] = tmp) is never reached.
fun swap(l: MutableList<Int>, i: Int, j: Int) {
    val tmp = l[i]
    <!INTERNAL_ERROR!>l[i] = l[j]<!>
    l[j] = tmp
}

// In-place reverse — depends on swap at each iteration.
fun reverseInPlace(l: MutableList<Int>) {
    var lo = 0
    var hi = l.size - 1
    while (lo < hi) {
        loopInvariants {
            0 <= lo && lo <= hi + 1 && hi < l.size
        }
        val tmp = l[lo]
        <!INTERNAL_ERROR!>l[lo] = l[hi]<!>
        l[hi] = tmp
        lo++
        hi--
    }
}

// Fill all elements with a constant.
fun fill(l: MutableList<Int>, v: Int) {
    var i = 0
    while (i < l.size) {
        loopInvariants { 0 <= i && i <= l.size }
        <!INTERNAL_ERROR!>l[i] = v<!>
        i++
    }
}

// Selection sort — requires both element comparison and swap.
fun selectionSort(l: MutableList<Int>) {
    postconditions<Unit> {}
    var i = 0
    while (i < l.size) {
        loopInvariants { 0 <= i && i <= l.size }
        var minIdx = i
        var j = i + 1
        while (j < l.size) {
            loopInvariants { i < j && j <= l.size && i <= minIdx && minIdx < l.size }
            if (l[j] < l[minIdx]) minIdx = j
            j++
        }
        val tmp = l[i]; <!INTERNAL_ERROR!>l[i] = l[minIdx]<!>; l[minIdx] = tmp
        i++
    }
}

// Prefix sum — reads l[i], writes result[i] = accumulated sum.
fun prefixSum(l: List<Int>, result: MutableList<Int>) {
    var sum = 0
    var i = 0
    while (i < l.size) {
        loopInvariants { 0 <= i && i <= l.size }
        sum += l[i]
        <!INTERNAL_ERROR!>result[i] = sum<!>
        i++
    }
}
