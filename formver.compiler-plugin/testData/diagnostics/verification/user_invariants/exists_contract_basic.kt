// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A postcondition existential has to be proven, unlike a precondition one, which is
// only assumed.
//
// The first three state satisfiable properties of `Int` with witnesses 0, 2 and 1,
// and by the feature contract all three ought to verify. None of them does: the
// markers below record that every postcondition existential fails, including
// `exists<Int> { it == 0 }`. The fourth has a provably empty witness set and is the
// only one whose failure is correct. Because the tool fails all four alike, this file
// pins current behaviour, not intended behaviour.

<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsZeroPost<!>(): Int {
    postconditions<Int> {
        exists<Int> { it == 0 }
    }
    return 0
}<!>

<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsSquareIsFour<!>(): Int {
    postconditions<Int> {
        exists<Int> { it * it == 4 }
    }
    return 0
}<!>

<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsStrictlyBetween<!>(): Int {
    postconditions<Int> {
        exists<Int> { it > 0 && it < 2 }
    }
    return 0
}<!>

// The witness set is provably empty, so this one must not be provable.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsEmptyIntRange<!>(): Int {
    postconditions<Int> {
        exists<Int> { it > 0 && it < 0 }
    }
    return 0
}<!>
