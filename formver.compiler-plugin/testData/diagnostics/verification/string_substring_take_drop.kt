// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun <!VIPER_TEXT!>substringLength<!>(s: String, i: Int): Int {
    if (i < 0 || i > s.length) return -1
    val sub = s.substring(i)
    verify(<!VIPER_VERIFICATION_ERROR!>sub.length == s.length - i<!>)
    return sub.length
}

@AlwaysVerify
fun <!VIPER_TEXT!>takeLength<!>(s: String, n: Int): Int {
    if (n < 0) return -1
    val bound = if (n < s.length) n else s.length
    val prefix = s.take(n)
    verify(<!VIPER_VERIFICATION_ERROR!>prefix.length == bound<!>)
    return prefix.length
}

@AlwaysVerify
fun <!VIPER_TEXT!>dropLength<!>(s: String, n: Int): Int {
    if (n < 0) return -1
    val bound = if (n < s.length) n else s.length
    val rest = s.drop(n)
    verify(<!VIPER_VERIFICATION_ERROR!>rest.length == s.length - bound<!>)
    return rest.length
}
