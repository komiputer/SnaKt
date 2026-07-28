// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Opt
class Present(val v: Int) : Opt
class Absent(val v: Int) : Opt

// A nullable sealed subject whose `null` case is an explicit branch. Both `when`s below are
// genuinely exhaustive with no syntactic `else`, so the missing fallthrough really is unreachable
// and both functions really do return `Int`.
//
// Both fail to verify: the exhaustiveness trust is gated on the subject not being nullable, which
// does not distinguish "null is covered by a branch" from "null is unhandled", so the fallthrough
// falls back to a `UnitLit` whose type contradicts the declared `Int` return. The recorded
// verification errors are wrong; a sound gate would ask whether a `null` branch exists.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>explicitNull<!>(x: Opt?): Int = when (x) {
    null -> 0
    is Present -> x.v
    is Absent -> x.v
}<!>

<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>explicitNullSubjectVal<!>(x: Opt?): Int = when (val y = x) {
    null -> 0
    is Present -> y.v
    is Absent -> y.v
}<!>
