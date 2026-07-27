// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A second, independent example of forAll and exists combined in one specification,
// distinct from max_character.kt's "maximum is an upper bound and is attained"
// pattern: here the property is purely arithmetic, with no string/loop involved.
@AlwaysVerify
fun <!VIPER_TEXT!>rangeBoundedAndNonEmpty<!>(n: Int): Boolean {
    preconditions {
        n > 0
    }
    postconditions<Boolean> {
        forAll<Int> { (0 <= it && it < n) implies (it < n) } &&
                exists<Int> { 0 <= it && it < n }
    }
    return true
}
