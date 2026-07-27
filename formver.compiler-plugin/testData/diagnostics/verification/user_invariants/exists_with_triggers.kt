// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// `triggers(vararg)` is fully wired for `exists` (shared InvariantBuilder
// receiver with `forAll`; insertExistsFunctionCall collects triggers the
// same way; ExistsEmbedding.triggerExpressions passes through to
// Exp.Exists), but forall_with_triggers.kt has no `exists` counterpart, and
// the KDoc on `triggers` still says "should be called within a forAll
// block" (Builtins.kt) even though `exists` accepts it identically.
//
// Silicon runs with MBQI off (E-matching only, no override in SnaKt's
// z3config.smt2). E-matching needs a ground pattern to instantiate a
// quantifier; a `forAll` can often infer one automatically from its body,
// but a bare postcondition `exists` may not get an automatic trigger the
// same way, which is one plausible explanation for the bare-postcondition-
// existential failures pinned in exists_postcondition_grounding.kt. This
// pair settles it: without a trigger (existsWithoutTriggerBarePostcondition,
// same failing shape as exists_postcondition_grounding.kt) versus with an
// explicit trigger (existsWithSimpleTriggerBarePostcondition). If the
// triggered version verifies where the untriggered one doesn't, the gap is
// "exists needs triggers, which are wired but undocumented and untested"
// rather than "exists can't prove a postcondition witness at all."
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsWithoutTriggerBarePostcondition<!>(): Int {
    postconditions<Int> {
        exists<Int> { it * it == 4 }
    }
    return 0
}<!>

@AlwaysVerify
fun <!VIPER_TEXT!>existsWithSimpleTriggerBarePostcondition<!>(): Int {
    postconditions<Int> {
        exists<Int> {
            triggers(it * it)
            it * it == 4
        }
    }
    return 0
}
