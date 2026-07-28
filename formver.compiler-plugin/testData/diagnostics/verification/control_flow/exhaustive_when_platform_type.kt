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
// The frontend still classifies this `when` as exhaustive over `Foo`'s subtypes, and the
// plugin trusts that classification: the missing fallthrough becomes `inhale false`.
// `JProvider.get()` returns null, so the fallthrough is in fact reachable and this function
// throws `NoWhenBranchMatchedException` at runtime.
@AlwaysVerify
fun <!VIPER_TEXT!>describe<!>(): Int = when (JProvider.get()) {
    is Bar -> 1
    is Baz -> 2
}
