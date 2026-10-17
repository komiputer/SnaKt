// FULL_JDK

// #247: Class/companion-qualified constant references are unsupported.
// visitResolvedQualifier only handles the Unit qualifier. All other qualified lookups
// (Int, Long, Char companion objects) hit handleUnimplementedElement.
// The INTERNAL_ERROR lands on the TYPE QUALIFIER (e.g. `Int` in `Int.MIN_VALUE`),
// not on the whole expression. Only the FIRST occurrence in each function is marked.

// Direct access to Int companion bounds.
fun intMinValue(): Int = <!INTERNAL_ERROR!>Int<!>.MIN_VALUE

fun intMaxValue(): Int = <!INTERNAL_ERROR!>Int<!>.MAX_VALUE

// Long companion bounds.
fun longMinValue(): Long = <!INTERNAL_ERROR!>Long<!>.MIN_VALUE

fun longMaxValue(): Long = <!INTERNAL_ERROR!>Long<!>.MAX_VALUE

// Char companion bound.
fun charMinValue(): Char = <!INTERNAL_ERROR!>Char<!>.MIN_VALUE

fun charMaxValue(): Char = <!INTERNAL_ERROR!>Char<!>.MAX_VALUE

// Idiomatic max-scan: accumulator initialized to Int.MIN_VALUE.
// After the crash on `Int` in the initializer, the while loop is never reached.
fun maxElement(l: List<Int>): Int {
    var max = <!INTERNAL_ERROR!>Int<!>.MIN_VALUE
    var i = 0
    while (i < l.size) {
        if (l[i] > max) max = l[i]
        i++
    }
    return max
}

// Idiomatic min-scan: accumulator initialized to Int.MAX_VALUE.
fun minElement(l: List<Int>): Int {
    var min = <!INTERNAL_ERROR!>Int<!>.MAX_VALUE
    var i = 0
    while (i < l.size) {
        if (l[i] < min) min = l[i]
        i++
    }
    return min
}

// Overflow guard using Int.MAX_VALUE in a condition: the crash on the first `Int`
// qualifier stops processing; the `Int.MAX_VALUE` in the else branch is never reached.
fun safeDoubleOrMax(n: Int): Int {
    return if (n > <!INTERNAL_ERROR!>Int<!>.MAX_VALUE / 2) Int.MAX_VALUE else n * 2
}

// Loop using Int.MAX_VALUE as a guard: first `Int` qualifier in the condition crashes;
// the `return Int.MAX_VALUE` inside the loop is never processed.
fun clampSum(l: List<Int>): Int {
    var sum = 0
    var i = 0
    while (i < l.size) {
        if (sum > <!INTERNAL_ERROR!>Int<!>.MAX_VALUE - l[i]) return Int.MAX_VALUE
        sum += l[i]
        i++
    }
    return sum
}
