// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.*

@AlwaysVerify
fun <!VIPER_TEXT!>forAllOutsideExists<!>() {
    verify(forAll<Int> { x ->
        exists<Int> { y -> y > x }
    })
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsOutsideForAll<!>() {
    verify(exists<Int> { x ->
        x > 0
        forAll<Int> { y -> y + x > y }
    })
}

@AlwaysVerify
fun <!VIPER_TEXT!>existsInsideExists<!>() {
    verify(exists<Int> { x ->
        exists<Int> { y -> x + y == 10 }
    })
}
