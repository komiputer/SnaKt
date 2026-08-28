// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.verify

fun <!VIPER_TEXT!>assumptionDoesNotLeakBackwards<!>(x: Int?): Int {
    verify(<!VIPER_VERIFICATION_ERROR!>x != null<!>)
    return x!!
}
