// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

@AlwaysVerify
fun <!VIPER_TEXT!>contradictoryBodyMustNotVerify<!>() {
    verify(<!VIPER_VERIFICATION_ERROR!>exists<Int> { it > 0 && it < 0 }<!>)
}

@AlwaysVerify
fun <!VIPER_TEXT!>multiStatementBodyIsConjunctionNotTwoQuantifiers<!>() {
    verify(<!VIPER_VERIFICATION_ERROR!>exists<Int> {
        it > 5
        it < 3
    }<!>)
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsMustNotLicenseTheUniversal<!>(): Int {
    preconditions {
        exists<Int> { it > 0 }
    }
    // The precondition only hands us a witness x with x > 0; it must not let us
    // assume that every value is positive.
    verify(<!VIPER_VERIFICATION_ERROR!>forAll<Int> { it > 0 }<!>)
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>triggersMustNotChangeTruthValue<!>() {
    verify(<!VIPER_VERIFICATION_ERROR!>exists<Int> {
        triggers(it * it)
        it > 0 && it < 0
    }<!>)
}

@AlwaysVerify
fun <!VIPER_TEXT!>negationOfSatisfiableExistsMustNotVerify<!>() {
    verify(<!VIPER_VERIFICATION_ERROR!>!exists<Int> { it > 0 }<!>)
}

@AlwaysVerify
fun <!VIPER_TEXT!>negationOfUnsatisfiableExistsMustVerify<!>() {
    verify(!exists<Int> { it > 0 && it < 0 })
}
