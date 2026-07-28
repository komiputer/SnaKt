// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

<!INTERNAL_ERROR, INTERNAL_ERROR, VIPER_VERIFICATION_ERROR!><!VIPER_TEXT!>enum class Colour<!> { RED, GREEN }<!>

// An exhaustive `when` over an enum subject with no `else`, the shape the commit message names as
// flowing through the same path as the sealed-interface case.
@AlwaysVerify
fun rank(c: Colour): Int = when (c) {
    Colour.RED -> 1
    Colour.GREEN -> 2
}

// The `Boolean` subject case, also named in the commit message.
@AlwaysVerify
fun <!VIPER_TEXT!>toInt<!>(b: Boolean): Int = when (b) {
    true -> 1
    false -> 0
}
