fun test(flag: Boolean): String {
    val f = JavaSource.get(flag)
    return when (f) {
        is Bar -> "bar"
        is Baz -> "baz"
    }
}

fun main() {
    println(test(false))
}
