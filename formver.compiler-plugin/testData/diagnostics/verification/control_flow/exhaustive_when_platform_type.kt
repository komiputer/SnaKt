// FULL_JDK
// FILE: JProvider.java

public class JProvider {
    public static Foo get() {
        return null;
    }
}

// FILE: test.kt

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Foo
object Bar : Foo
object Baz : Foo

// The subject arrives from Java as the platform type `Foo!`, so it may be null at runtime.
// The frontend still classifies this `when` as exhaustive over `Foo`'s subtypes, but the
// plugin no longer trusts that classification for a nullable subject: the missing fallthrough
// falls back to the old `UnitLit` path instead of `inhale false`. That correctly fails
// verification (spurious `Int` vs `Unit` postcondition mismatch) instead of unsoundly accepting
// a function that can throw `NoWhenBranchMatchedException` at runtime.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>describe<!>(): Int = when (JProvider.get()) {
    is Bar -> 1
    is Baz -> 2
}<!>
