// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Shape
class Circle(val r: Int) : Shape
class Square(val s: Int) : Shape

class Box(val n: Int)

sealed class Color
class Red : Color()
class Green : Color()

sealed interface Only
class TheOne(val v: Int) : Only

// The result of an exhaustive `when` may be a reference type, not just a primitive one.
@AlwaysVerify
fun <!VIPER_TEXT!>pick<!>(sh: Shape, a: Box, b: Box): Box = when (sh) {
    is Circle -> a
    is Square -> b
}

// A nullable sealed subject is exhaustive once the `null` case is covered too.
@AlwaysVerify
fun <!VIPER_TEXT!>sizeOrZero<!>(sh: Shape?): Int = when (sh) {
    null -> 0
    is Circle -> sh.r
    is Square -> sh.s
}

// Sealed classes behave the same as sealed interfaces.
@AlwaysVerify
fun <!VIPER_TEXT!>code<!>(c: Color): Int = when (c) {
    is Red -> 0
    is Green -> 1
}

// A hierarchy with a single implementation still leaves a fallthrough for the compiler to prove
// impossible.
@AlwaysVerify
fun <!VIPER_TEXT!>unwrap<!>(o: Only): Int = when (o) {
    is TheOne -> o.v
}

// Exhaustiveness is not a sealed-hierarchy-only notion: a `when` over both Boolean values is
// exhaustive without an `else` as well, and must be handled the same way.
@AlwaysVerify
fun <!VIPER_TEXT!>fromBool<!>(b: Boolean): Int = when (b) {
    true -> 1
    false -> 0
}

// The result of an exhaustive `when` is usable where the declared type is required, including as
// the operand of further arithmetic.
@AlwaysVerify
fun <!VIPER_TEXT!>twice<!>(sh: Shape): Int = 2 * when (sh) {
    is Circle -> sh.r
    is Square -> sh.s
}
