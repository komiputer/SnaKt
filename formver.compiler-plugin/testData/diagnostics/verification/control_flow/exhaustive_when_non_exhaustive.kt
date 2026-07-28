// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Cmd
class Push(val v: Int) : Cmd
class Pop(val v: Int) : Cmd
class Peek(val v: Int) : Cmd

// `Peek` is covered by no branch and there is no `else`, so Kotlin's own exhaustiveness check must
// reject this before SnaKt's lowering runs. Trusting FIR's exhaustiveness decision must not
// suppress that diagnostic.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>run<!>(c: Cmd): Int = <!NO_ELSE_IN_WHEN!>when<!> (c) {
    is Push -> c.v
    is Pop -> c.v
}<!>
