// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.postconditions

// #256: Type-parameter upper bounds are erased during embedding.
// TypeBuilder.embedTypeWithBuilder maps ConeTypeParameterType to (isNullable=true, any()),
// discarding declared bounds. The comparison operators (a > b) for T: Comparable<T>
// CONVERT to Viper (they go through the comparison-expression else path which falls back
// to a generic method call embedding). Verification fails because no subtype fact for
// T <: Comparable is registered, so the callee precondition cannot be discharged.
//
// Exception: binarySearchExists uses l[mid].compareTo(target) DIRECTLY (not via operator)
// which goes through visitFunctionCall and crashes with INTERNAL_ERROR.
//
// Positive baseline: generic functions that do NOT call methods on T work correctly.

fun <T> <!VIPER_TEXT!>identityGeneric<!>(t: T): T = t

fun <T> <!VIPER_TEXT!>swapGeneric<!>(a: T, b: T): T {
    val tmp = a
    return b
}

// The comparison `a > b` converts (else-path in visitComparisonExpression), but
// verification fails: no isSubtype(typeOf(a), Comparable) fact is available.
fun <T : Comparable<T>> <!VIPER_TEXT!>maxOf2<!>(a: T, b: T): T = if (a > b) a else b

fun <T : Comparable<T>> <!VIPER_TEXT!>minOf2<!>(a: T, b: T): T = if (a < b) a else b

fun <T : Comparable<T>> <!VIPER_TEXT!>isSorted<!>(l: List<T>): Boolean {
    var i = 0
    while (i < l.size - 1) {
        if (l[i] > l[i + 1]) return false
        i++
    }
    return true
}

fun <T : Comparable<T>> <!VIPER_TEXT!>clamp<!>(value: T, lo: T, hi: T): T {
    if (value < lo) return lo
    if (value > hi) return hi
    return value
}

// Direct `.compareTo()` call goes through visitFunctionCall (not visitComparisonExpression)
// and crashes the conversion entirely.
<!INTERNAL_ERROR!>@AlwaysVerify
fun <T : Comparable<T>> binarySearchExists(l: List<T>, target: T): Boolean {
    postconditions<Boolean> { res ->
        res implies (l.size > 0)
    }
    var lo = 0
    var hi = l.size - 1
    while (lo <= hi) {
        val mid = lo + (hi - lo) / 2
        val cmp = l[mid].compareTo(target)
        when {
            cmp == 0 -> return true
            cmp < 0  -> lo = mid + 1
            else     -> hi = mid - 1
        }
    }
    return false
}<!>
