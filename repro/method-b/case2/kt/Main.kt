package app2

import klib.*
import jlib.JProvider

// Subject arrives as a Java platform type `Foo!`. No `else`; FIR's exhaustiveness
// decision over the sealed type is what we are probing.
fun describeNullable(): String = when (val x = JProvider.nullable()) {
    is Bar -> "bar"
    is Baz -> "baz"
}

// Subject's static type is `Foo` via an unchecked Java generic coercion; the runtime
// value is not a Foo at all.
fun describeCoerced(o: Any): String {
    val x: Foo = JProvider.coerce(o)
    return when (x) {
        is Bar -> "bar"
        is Baz -> "baz"
    }
}

fun main() {
    println(runCatching { describeNullable() })
    println(runCatching { describeCoerced("not a Foo") })
}
