// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.NeverConvert

@NeverConvert
fun id(x: Int?): Int? = x

fun <!VIPER_TEXT!>notNullAssertion<!>(x: Int?): Int {
    return x!!
}

fun <!VIPER_TEXT!>notNullAssertionOfCall<!>(): Int {
    return id(3)!!
}

fun <!VIPER_TEXT!>notNullAssertionThenUse<!>(x: Int?): Int {
    val y = x!!
    return y + 1
}

fun <!VIPER_TEXT!>notNullAssertionInTry<!>(x: Int?): Int {
    try {
        return x!!
    } catch (e: Exception) {
        return 0
    }
}
