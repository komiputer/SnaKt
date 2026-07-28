// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

<!INTERNAL_ERROR, INTERNAL_ERROR, INTERNAL_ERROR, VIPER_VERIFICATION_ERROR!><!VIPER_TEXT!>enum class Color<!> { RED, GREEN, BLUE }<!>

// A `when` covering every entry of an enum is exhaustive without an `else`, so the function is
// total and verifies as always returning an Int.
@AlwaysVerify
fun rank(c: Color): Int = when (c) {
    Color.RED -> 0
    Color.GREEN -> 1
    Color.BLUE -> 2
}

// Totality is trusted, but branch bodies are still checked: the BLUE branch yields -1, so the
// assertion below must fail to verify.
@AlwaysVerify
fun rankNonNeg(c: Color): Int {
    val r = when (c) {
        Color.RED -> 0
        Color.GREEN -> 1
        Color.BLUE -> -1
    }
    verify(r >= 0)
    return r
}
