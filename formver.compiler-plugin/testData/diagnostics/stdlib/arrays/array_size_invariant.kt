// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.loopInvariants
import org.jetbrains.kotlin.formver.plugin.verify

// #253: size/length non-negativity invariant does not extend to native arrays.
// CollectionSizeFieldEmbedding asserts size >= 0 only for Collection inheritors.
// IntArray, Array<T>, and similar native arrays bypass this path.
//
// Key finding: IntArray IS embedded (ConeClassLikeType -> embedClass creates a generic
// class embedding), so functions using IntArray.size convert to Viper successfully.
// The bug is a VERIFICATION failure, not a conversion crash.
//
// Positive baseline: List.size non-negativity IS provided — loop invariant holds.

<!INTERNAL_ERROR!>fun listSizeNonNegative(l: List<Int>): Int {
    var i = 0
    while (i < l.size) {
        loopInvariants {
            0 <= i
            i <= l.size
        }
        i++
    }
    return i
}<!>

// IntArray: same loop, but size non-negativity invariant is ABSENT.
// The loop invariant `i <= a.size` cannot be established initially without size >= 0.
// This converts to Viper but fails verification (VIPER_VERIFICATION_ERROR in full mode).
fun <!VIPER_TEXT!>intArrayLinearScan<!>(a: IntArray): Int {
    verify(a.size >= 0)
    var i = 0
    while (i < a.size) {
        loopInvariants {
            0 <= i
            i <= a.size
        }
        i++
    }
    return i
}

// Generic Array<T> has the same issue.
fun <!VIPER_TEXT!>arrayOfTSize<!>(a: Array<Int>): Int {
    verify(a.size >= 0)
    return a.size
}

// Even a simple `a.size` read converts; the non-negativity is only absent when used
// in invariants or post-conditions that depend on it.
fun <!VIPER_TEXT!>intArraySizeRead<!>(a: IntArray): Int {
    return a.size
}

// Two-array size equality: converts, but the solver lacks invariants for either side.
fun <!VIPER_TEXT!>sameLength<!>(a: IntArray, b: IntArray): Boolean {
    return a.size == b.size
}
