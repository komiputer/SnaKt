// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// The no-arg setOf()/mutableSetOf() overloads always construct an empty set.
// This does not cover HashSet(): as a constructor call it embeds under a
// distinct ConstructorKotlinName, which the functionName-based dispatch used
// here does not match.

@AlwaysVerify
fun <!VIPER_TEXT!>set_of_is_empty<!>(): Int {
    val s = setOf<Int>()
    val arr = IntArray(1 - s.size)
    return arr[0]
}

@AlwaysVerify
fun <!VIPER_TEXT!>mutable_set_of_is_empty<!>(): Int {
    val s = mutableSetOf<Int>()
    val arr = IntArray(1 - s.size)
    return arr[0]
}
