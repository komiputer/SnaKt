// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// `List.get` is embedded as a method call, unlike `String`'s indexing operator,
// so it is not pure and cannot appear in a quantifier body. The user has to be
// told that with a diagnostic; an unsupported construct inside a quantifier must
// not take down the compiler.
@NeverVerify
fun <!VERIFICATION_SKIPPED!>existsOverListElements<!>(l: List<Int>, res: Int): Int {
    postconditions<Int> {
        exists<Int> { i -> <!PURITY_VIOLATION!>0 <= i && i < l.size && l[i] == l[res]<!> }
    }
    return 0
}

// The same holds for the universal quantifier: the body purity rule is shared,
// so both must reject it the same way.
@NeverVerify
fun <!VERIFICATION_SKIPPED!>forAllOverListElements<!>(l: List<Int>, res: Int): Int {
    postconditions<Int> {
        forAll<Int> { i -> <!PURITY_VIOLATION!>(0 <= i && i < l.size) implies (l[i] <= l[res])<!> }
    }
    return 0
}
