// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// Basic existential satisfiability contract cases: a literal witness, an arithmetic
// witness, and a conjunction-bounded witness should all verify. These are stated as
// preconditions (merely assumed, following the same convention as exists.kt's
// simpleExists), since the point here is just that the tool accepts them without
// crashing or rejecting a satisfiable existential.

@AlwaysVerify
fun <!VIPER_TEXT!>existsLiteralWitness<!>(): Int {
    preconditions {
        exists<Int> { it == 0 }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsSquareWitness<!>(): Int {
    preconditions {
        exists<Int> { it * it == 4 }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsRangeWitness<!>(): Int {
    preconditions {
        exists<Int> { it > 0 && it < 2 }
    }
    return 0
}

// The witness set is provably empty (no integer is both > 0 and < 0). Unlike the
// cases above, this is stated as a postcondition so the verifier is actually forced
// to discharge it (a precondition is merely assumed and never proven for a
// callee-less method, as exists_char_bound_contract.kt's first draft discovered the
// hard way): the tool must fail to verify this rather than silently accepting it.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsEmptyDomain<!>(): Boolean {
    postconditions<Boolean> {
        exists<Int> { it > 0 && it < 0 }
    }
    return true
}<!>
