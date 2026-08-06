// FULL_JDK
// RENDER_PREDICATES

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// A predicate's Viper form takes exactly one argument, the subject, and a call site forwards only
// the subject. A non-receiver value parameter therefore cannot mean anything: `bound` is bound
// once, when the declaration is converted, and the arguments at the two call sites below are
// discarded. Accepting the declaration is the bug; it must be rejected.
class Box(val v: Int)

// Two call sites below each embed and diagnose this declaration independently (predicates are
// embedded lazily, per caller), so the malformed declaration is marked once per caller.
fun Box.<!MALFORMED_PREDICATE_DECLARATION, MALFORMED_PREDICATE_DECLARATION!>atMost<!>(bound: Int): Boolean = predicate {
    v <= bound
}

// The two call sites differ only in the argument. If both verify, the argument is not part of the
// predicate's meaning and the caller of `atMostTen` has been given a guarantee it never asked for.
@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>atMostTen<!>(b: Box) {
    preconditions {
        b.atMost(10)
    }
}

@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>atMostZero<!>(b: Box) {
    preconditions {
        b.atMost(0)
    }
}

// A value parameter that is never mentioned in the body is equally meaningless, and must be
// rejected on the same grounds rather than tolerated because nothing reads it.
fun Box.<!MALFORMED_PREDICATE_DECLARATION!>positiveIgnoringArg<!>(unused: Boolean): Boolean = predicate {
    v > 0
}

@AlwaysVerify
fun <!VERIFICATION_SKIPPED!>usePositiveIgnoringArg<!>(b: Box) {
    preconditions {
        b.positiveIgnoringArg(true)
    }
}
