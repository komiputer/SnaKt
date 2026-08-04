// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

class Marker

@AlwaysVerify
fun <!VIPER_TEXT!>existsOverRefTypeNeedsWellTypedWitness<!>() {
    verify(<!VIPER_VERIFICATION_ERROR!>exists<Marker> { false }<!>)
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsOverNullableRefTypeGuardNotTooStrong<!>() {
    verify(exists<Marker?> { it == null })
}
