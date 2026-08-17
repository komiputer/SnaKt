// FULL_JDK

fun <!VIPER_TEXT!>alwaysThrows<!>(): Int {
    throw Exception("always throws")
}

fun <!VIPER_TEXT!>requirePositive<!>(x: Int): Int {
    if (x <= 0) {
        throw IllegalArgumentException("x must be positive")
    }
    return x
}

fun <!VIPER_TEXT!>throwInBranch<!>(b: Boolean): Int {
    return if (b) {
        42
    } else {
        throw Exception("not b")
    }
}
