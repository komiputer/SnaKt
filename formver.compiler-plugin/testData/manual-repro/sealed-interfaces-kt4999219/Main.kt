fun test(e: Expr): String = when (e) {
    is A -> "a"
    is B -> "b"
}
