package lib

sealed interface Expr
class Const(val value: Int) : Expr
class Neg(val operand: Const) : Expr
