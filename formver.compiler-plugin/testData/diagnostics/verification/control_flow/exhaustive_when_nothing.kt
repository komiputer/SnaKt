// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// `x`'s static type is `Nothing` (inferred from `TODO()`), so the (zero-branch, no-`else`)
// `when` over it should be classified `ExhaustiveAsNothing` rather than `ProperlyExhaustive`
// by FIR. Per known-issues.md Issue 1, StmtConversionVisitor.kt:172 does not special-case this
// variant either, so the fallthrough is expected to lower to the old fabricated `UnitLit`,
// producing a spurious rejection on this unreachable path even though `uninhabited` is
// actually total (unreachable, since `Nothing` has no instances).
// SHOULD-VERIFY (not should-fail): this case is expected to currently, incorrectly, fail.
@AlwaysVerify
fun <!VIPER_TEXT!>uninhabited<!>(): Int {
    val x = TODO()
    return <!RETURN_TYPE_MISMATCH!>when (x) {}<!>
}
