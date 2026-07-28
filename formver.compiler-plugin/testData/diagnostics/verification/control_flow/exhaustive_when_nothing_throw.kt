// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

fun boom(): Nothing = <!INTERNAL_ERROR!>throw RuntimeException()<!>

// The subject's static type is `Nothing`, so the `when` is vacuously exhaustive with zero branches
// and no `else`. The function is total (the `when` is unreachable), so it should verify as always
// returning an Int.
@AlwaysVerify
fun <!VIPER_TEXT!>exhaustiveAsNothing<!>(): Int = <!RETURN_TYPE_MISMATCH!>when (boom()) {
}<!>
