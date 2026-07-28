package app

import lib.*

fun eval(e: Expr): Int = when (e) {
    is Const -> e.value
    is Neg -> -e.operand.value
}

fun main() {
    println(eval(Const(3)))
    val z: Expr = Class.forName("lib.Zero").getField("INSTANCE").get(null) as Expr
    println(eval(z))
}
