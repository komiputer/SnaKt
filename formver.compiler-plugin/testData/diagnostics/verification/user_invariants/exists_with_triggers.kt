// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

// `triggers` is available inside an `exists` body exactly as inside `forAll`.
@AlwaysVerify
fun <!VIPER_TEXT!>existsWithSimpleTrigger<!>(): Int {
    preconditions {
        exists<Int> {
            triggers(it * it)
            it * it == 0
        }
    }
    postconditions<Int> {
        exists<Int> {
            triggers(it * it)
            it * it == 0
        }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsWithMultipleTriggers<!>(): Int {
    preconditions {
        exists<Int> {
            triggers(it * it, it + 1)
            it * it == 0 && it + 1 == 1
        }
    }
    postconditions<Int> {
        exists<Int> {
            triggers(it * it, it + 1)
            it * it == 0 && it + 1 == 1
        }
    }
    return 0
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsWithTriggerInLoopInvariant<!>(str: String): Int {
    preconditions {
        str.length > 0
    }
    var i = 1
    while (i < str.length) {
        loopInvariants {
            1 <= i && i <= str.length
            exists<Int> {
                triggers(str[it])
                0 <= it && it < i
            }
        }
        i += 1
    }
    return i
}

// A trigger term that does not mention the bound variable is not a valid Viper
// trigger. The plugin passes triggers through unchecked, so this must surface as
// a diagnostic from Viper rather than being accepted or crashing the compiler.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>existsWithTriggerNotMentioningBoundVariable<!>(n: Int): Int {
    preconditions {
        exists<Int> {
            triggers(n * n)
            it == 0
        }
    }
    return 0
}<!>
