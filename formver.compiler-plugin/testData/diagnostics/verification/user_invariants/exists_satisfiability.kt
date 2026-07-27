// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

@AlwaysVerify
fun <!VIPER_TEXT!>existsIntEqualsZero<!>(): Int {
    preconditions {
        exists<Int> { it == 0 }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsSquareEqualsFour<!>(): Int {
    preconditions {
        exists<Int> { it * it == 4 }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsIntInOpenRange<!>(): Int {
    preconditions {
        exists<Int> { it > 0 && it < 2 }
    }
    return 0
}

// The witness set is provably empty: no integer is both > 0 and < 0. Unlike
// an unsatisfiable precondition (which is merely assumed and never checked
// for satisfiability at the definition site), an unsatisfiable postcondition
// must actually be proven at every return point, so this should fail to
// verify.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsEmptyWitnessSetPostcondition<!>(): Int {
    postconditions<Int> {
        exists<Int> { it > 0 && it < 0 }
    }
    return 0
}<!>
