// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// A postcondition existential has to be proven, unlike a precondition one, which is
// only assumed. These three state satisfiable properties of `Int` and must verify.

@AlwaysVerify
fun <!VIPER_TEXT!>existsZeroPost<!>(): Int {
    postconditions<Int> {
        exists<Int> { it == 0 }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsSquareIsFour<!>(): Int {
    postconditions<Int> {
        exists<Int> { it * it == 4 }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsStrictlyBetween<!>(): Int {
    postconditions<Int> {
        exists<Int> { it > 0 && it < 2 }
    }
    return 0
}

// The witness set is provably empty, so the existential must not be provable.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsEmptyIntRange<!>(): Int {
    postconditions<Int> {
        exists<Int> { it > 0 && it < 0 }
    }
    return 0
}<!>
