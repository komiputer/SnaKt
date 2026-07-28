// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Tok
class Num(val v: Int) : Tok
class Sym(val v: Int) : Tok

// An exhaustive `when` in a local function body must be treated the same as one at top level.
@AlwaysVerify
fun viaLocalFun(t: Tok): Int {
    <!INTERNAL_ERROR!>fun <!VIPER_TEXT!>score<!>(x: Tok): Int = when (x) {
        is Num -> x.v
        is Sym -> x.v
    }<!>
    return score(t)
}

// Same, but inside a lambda body.
<!INTERNAL_ERROR!>@AlwaysVerify
fun viaLambda(t: Tok): Int {
    val f = { x: Tok ->
        when (x) {
            is Num -> x.v
            is Sym -> x.v
        }
    }
    return f(t)
}<!>
