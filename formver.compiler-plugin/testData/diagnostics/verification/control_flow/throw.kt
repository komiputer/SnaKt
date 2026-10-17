// FULL_JDK

// #255: `throw` expressions are not lowered — StmtConversionVisitor has no visitThrowExpression.
// The INTERNAL_ERROR marker lands on the throw expression itself; the containing function
// converts up to that point but no VIPER_TEXT is emitted (conversion is considered failed).
// The tractable first fix is lowering an uncaught throw to `inhale false` (unconditional abort).

// Simplest case: unconditional throw with no surrounding code.
fun alwaysThrows(): Nothing {
    <!INTERNAL_ERROR!>throw IllegalArgumentException("always")<!>
}

// Guard pattern: most common real-world use — enforce a precondition at runtime.
fun requirePositive(n: Int): Int {
    if (n <= 0) <!INTERNAL_ERROR!>throw IllegalArgumentException("non-positive: $n")<!>
    return n
}

// Multiple guard sites: only the FIRST throw is reached during conversion; the second
// is never visited because the crash aborts the function after the first.
fun requireInRange(n: Int, lo: Int, hi: Int): Int {
    if (n < lo) <!INTERNAL_ERROR!>throw IllegalArgumentException("below lo")<!>
    if (n > hi) throw IllegalArgumentException("above hi")
    return n
}

// Throw as the result of a `when` branch — exhaustive when, one arm throws.
fun classifySign(n: Int): String {
    return when {
        n > 0 -> "positive"
        n < 0 -> "negative"
        else -> <!INTERNAL_ERROR!>throw ArithmeticException("zero is unclassified")<!>
    }
}

// Throw in a nested conditional block.
fun nested(n: Int, m: Int): Int {
    if (n > 0) {
        if (m <= 0) <!INTERNAL_ERROR!>throw IllegalArgumentException("m must be positive when n > 0")<!>
        return n + m
    }
    return 0
}

// Multiple return paths, one of which is a throw.
fun divideOrThrow(a: Int, b: Int): Int {
    if (b == 0) <!INTERNAL_ERROR!>throw ArithmeticException("division by zero")<!>
    return a / b
}

// Throw at the end of an else-if chain — code before the throw converts fine,
// but no Viper output is generated for the function because the throw path crashes.
fun parseSign(s: String): Int {
    if (s == "positive") return 1
    if (s == "negative") return -1
    if (s == "zero") return 0
    <!INTERNAL_ERROR!>throw IllegalArgumentException("unknown sign: $s")<!>
}
