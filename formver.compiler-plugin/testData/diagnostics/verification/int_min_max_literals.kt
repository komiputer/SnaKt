// FULL_JDK

fun <!VIPER_TEXT!>intMinValue<!>(): Int = Int.MIN_VALUE

fun <!VIPER_TEXT!>intMaxValue<!>(): Int = Int.MAX_VALUE

fun <!VIPER_TEXT!>clampLower<!>(x: Int): Int = if (x < Int.MIN_VALUE) Int.MIN_VALUE else x

fun <!VIPER_TEXT!>isMaxInt<!>(x: Int): Boolean = x == Int.MAX_VALUE
