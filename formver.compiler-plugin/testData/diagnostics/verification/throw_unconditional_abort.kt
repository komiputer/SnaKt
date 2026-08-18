// FULL_JDK

// An uncaught throw is lowered to an abrupt exit: the state is marked
// unreachable past that point, so verification does not attempt to
// establish the declared return type or any postcondition on that path.

fun <!VIPER_TEXT!>alwaysThrows<!>(): Int {
    throw IllegalStateException("boom")
}

fun <!VIPER_TEXT!>throwsInBranch<!>(x: Int): Int {
    if (x < 0) {
        throw IllegalArgumentException("negative")
    }
    return x
}

fun <!VIPER_TEXT!>throwsInElseBranch<!>(x: Int): Int {
    return if (x >= 0) {
        x
    } else {
        throw IllegalArgumentException("negative")
    }
}
