// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.NeverConvert

// #257: Function objects can't be linearized to a Viper value.
// LinearizationVisitor.visitLambdaExp is a TODO — any path that requires a lambda
// AS A STORED VALUE (passed to a higher-order function, returned, or assigned)
// throws NotImplementedError and crashes the entire function conversion.
// Exception: a function that only *receives* a function-type parameter (but does not
// create a lambda at the call site) CAN convert — e.g. applyTwice below.

@NeverConvert
fun consumeInt(x: Int) {}

// Lambda passed to forEach — the lambda expression crashes when the call site is linearized.
<!INTERNAL_ERROR!>fun forEachUse(l: List<Int>) {
    l.forEach { consumeInt(it) }
}<!>

<!INTERNAL_ERROR!>fun mapUse(l: List<Int>): List<Int> {
    return l.map { it * 2 }
}<!>

<!INTERNAL_ERROR!>fun filterUse(l: List<Int>): List<Int> {
    return l.filter { it > 0 }
}<!>

<!INTERNAL_ERROR!>fun foldUse(l: List<Int>): Int {
    return l.fold(0) { acc, x -> acc + x }
}<!>

// Lambda assigned to a variable — the RHS (a lambda expression) needs linearization.
<!INTERNAL_ERROR!>fun lambdaVariable(): Int {
    val double: (Int) -> Int = { x -> x * 2 }
    return double(3)
}<!>

// Lambda returned from a function — the function object needs to be stored in the return value.
<!INTERNAL_ERROR!>fun makeAdder(n: Int): (Int) -> Int {
    return { x -> x + n }
}<!>

<!INTERNAL_ERROR!>fun sumWithOffset(l: List<Int>, offset: Int): Int {
    val shifted = l.map { it + offset }
    return shifted.fold(0) { acc, x -> acc + x }
}<!>

// applyTwice TAKES a function parameter but does not CREATE a lambda at the call site —
// the f(x) calls go through implicitInvokeCall which is handled. Converts successfully.
fun <!VIPER_TEXT!>applyTwice<!>(f: (Int) -> Int, x: Int): Int {
    return f(f(x))
}

// let/run desugar to inline lambda calls — the lambda is created at the call site.
<!INTERNAL_ERROR!>fun useLetChain(n: Int): Int {
    return n.let { it + 1 }.let { it * 2 }
}<!>
