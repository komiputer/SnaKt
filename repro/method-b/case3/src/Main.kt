package case3

// KT-7301055 shape: an outer null-check narrows `foo`, then `when (val it = foo)` covers
// exactly the non-null subtypes with no `else`.
sealed interface Foo {
    object Bar : Foo
    object Baz : Foo
}

fun test(foo: Foo?) =
    if (foo == null) "null"
    else when (val it = foo) {
        is Foo.Bar -> "bar"
        is Foo.Baz -> "baz"
    }
