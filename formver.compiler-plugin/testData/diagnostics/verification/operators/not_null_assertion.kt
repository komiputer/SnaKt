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

// Catch routing is not implemented yet; this pins the unrouted behaviour (the
// assertion's null path is pruned, not caught) ahead of that work.
fun <!VIPER_TEXT!>notNullAssertionInTry<!>(x: Int?): Int {
    try {
        return x!!
    } catch (e: Exception) {
        return 0
    }
}

fun <T> <!VIPER_TEXT!>notNullAssertionOfTypeParameter<!>(t: T?): T {
    return t!!
}
