// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.loopInvariants
import org.jetbrains.kotlin.formver.plugin.verify

// #249: String content model has three gaps that block common patterns.
// Key finding: ALL functions below CONVERT to Viper successfully. The bugs are
// VERIFICATION failures, not conversion crashes.
//
// Gap 1: No String <: CharSequence subtype fact — `for (c in cs)` where cs: CharSequence
//         converts, but the iterator precondition (isSubtype(typeOf(cs), CharSequence()))
//         cannot be discharged at verification time.
// Gap 2: stringGet precondition discharge — index-bounds from loop invariants are not
//         automatically linked to the Viper seq-index precondition.
// Gap 3: Concat-length postcondition — no axiom for (a + b).length == a.length + b.length.

// --- Gap 1: CharSequence subtype ---

// Iterating a CharSequence parameter — the iterator() call converts to a Viper method
// call whose precondition requires the CharSequence subtype fact.
fun <!VIPER_TEXT!>iterateCharSequence<!>(cs: CharSequence): Int {
    var count = 0
    for (c in cs) {
        count++
    }
    return count
}

// CharSequence.length — converts; verification of downstream length-based invariants
// may fail if no CharSequence spec is available.
fun <!VIPER_TEXT!>charSequenceLength<!>(cs: CharSequence): Int {
    return cs.length
}

// Passing a String as CharSequence requires the String <: CharSequence subtype fact.
@AlwaysVerify
fun <!VIPER_TEXT!>passStringAsCharSequence<!>(s: String): Int {
    return iterateCharSequence(s)
}

// --- Gap 2: Index-bounds discharge ---

// Loop over a String using index access — the stringGet precondition (`0 <= i < |s|`)
// should be dischargeable from the loop invariant `0 <= i && i < s.length`,
// but currently is not linked and may fail at verification.
@AlwaysVerify
fun <!VIPER_TEXT!>indexedStringLoop<!>(s: String): Int {
    var i = 0
    var count = 0
    while (i < s.length) {
        loopInvariants {
            0 <= i
            i <= s.length
        }
        if (s[i] == 'a') count++
        i++
    }
    return count
}

// Two-pointer palindrome scan — both indices need bounds discharge from their invariants.
@AlwaysVerify
fun <!VIPER_TEXT!>twoPointerScan<!>(s: String): Boolean {
    var lo = 0
    var hi = s.length - 1
    while (lo < hi) {
        loopInvariants {
            0 <= lo && lo < s.length
            0 <= hi && hi < s.length
            lo <= hi
        }
        if (s[lo] != s[hi]) return false
        lo++
        hi--
    }
    return true
}

// --- Gap 3: Concat-length postcondition ---

// The length of (a + b) should equal a.length + b.length, but no such axiom exists.
@AlwaysVerify
fun <!VIPER_TEXT!>concatLength<!>(a: String, b: String): Int {
    val c = a + b
    verify(c.length == a.length + b.length)
    return c.length
}
