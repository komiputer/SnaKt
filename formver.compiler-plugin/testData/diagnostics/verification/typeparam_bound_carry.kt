// FULL_JDK

// #256: The declared upper bound of a type parameter is erased to `any()` when
// building its TypeBuilder, instead of carrying the ConeTypeParameterType's
// actual upper bound. Calling a member that only the bound (not Any) provides
// should therefore be rejected or mishandled by verification.

fun <T : Number> <!VIPER_TEXT!>toIntValue<!>(x: T): Int = <!VIPER_VERIFICATION_ERROR!>x.toInt()<!>

fun <T : CharSequence> <!VIPER_TEXT!>firstChar<!>(x: T): Char = <!VIPER_VERIFICATION_ERROR!>x[0]<!>

fun <T : Number> <!VIPER_TEXT!>sumWithBound<!>(x: T, y: T): Double = <!VIPER_VERIFICATION_ERROR!>x.toDouble()<!> + y.toDouble()
