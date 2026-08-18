// FULL_JDK

// #247: Companion-qualified constant references (Int.MIN_VALUE, Long.MAX_VALUE, etc.)
// are now supported. They are intercepted in visitPropertyAccessExpression before the
// qualifier is visited, and emitted as integer/char literals in Viper.

// Direct access to Int companion bounds.
fun <!VIPER_TEXT!>intMinValue<!>(): Int = Int.MIN_VALUE

fun <!VIPER_TEXT!>intMaxValue<!>(): Int = Int.MAX_VALUE

// Long companion bounds.
<!VIPER_VERIFICATION_ERROR!>fun <!VIPER_TEXT!>longMinValue<!>(): Long = Long.MIN_VALUE<!>

<!VIPER_VERIFICATION_ERROR!>fun <!VIPER_TEXT!>longMaxValue<!>(): Long = Long.MAX_VALUE<!>

// Char companion bound.
fun <!VIPER_TEXT!>charMinValue<!>(): Char = Char.MIN_VALUE

fun <!VIPER_TEXT!>charMaxValue<!>(): Char = Char.MAX_VALUE

// Idiomatic max-scan: accumulator initialized to Int.MIN_VALUE.
<!VIPER_VERIFICATION_ERROR!>fun <!VIPER_TEXT!>maxElement<!>(l: List<Int>): Int {
    var max = Int.MIN_VALUE
    var i = 0
    while (i < l.size) {
        if (l[i] > max) max = l[i]
        i++
    }
    return max
}<!>

// Idiomatic min-scan: accumulator initialized to Int.MAX_VALUE.
<!VIPER_VERIFICATION_ERROR!>fun <!VIPER_TEXT!>minElement<!>(l: List<Int>): Int {
    var min = Int.MAX_VALUE
    var i = 0
    while (i < l.size) {
        if (l[i] < min) min = l[i]
        i++
    }
    return min
}<!>

// Overflow guard using Int.MAX_VALUE in a condition.
fun <!VIPER_TEXT!>safeDoubleOrMax<!>(n: Int): Int {
    return if (n > Int.MAX_VALUE / 2) Int.MAX_VALUE else n * 2
}

// Loop using Int.MAX_VALUE as a guard.
<!VIPER_VERIFICATION_ERROR!>fun <!VIPER_TEXT!>clampSum<!>(l: List<Int>): Int {
    var sum = 0
    var i = 0
    while (i < l.size) {
        if (sum > Int.MAX_VALUE - l[i]) return Int.MAX_VALUE
        sum += l[i]
        i++
    }
    return sum
}<!>
