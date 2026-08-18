// FULL_JDK

// #249: String content model is missing a String <: CharSequence subtype fact in the
// type domain. Split out of PR #45 (companion PR jesyspa/SnaKt#45), which found that
// conversion itself succeeds for these calls; the gap is that verification cannot yet
// discharge the CharSequence-typed preconditions this generates against a String
// argument, because there is no explicit subtype axiom. This fixture only exercises
// conversion (this repo's fast test loop); the golden below records the current
// conversion-only behaviour, not verification.

fun <!VIPER_TEXT!>takesCharSequence<!>(cs: CharSequence): Int = cs.length

fun <!VIPER_TEXT!>passStringAsCharSequence<!>(s: String): Int = <!VIPER_VERIFICATION_ERROR!>takesCharSequence(s)<!>
