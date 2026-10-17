// FULL_JDK

// #251 (parent #46): Set / MutableSet have no StdLibConverter interface entries.
// ListInterface and MutableListInterface exist and drive get/subList/add specs for
// lists, but there is no matching SetInterface / MutableSetInterface, so any
// set-specific dispatch (as opposed to the generic Collection interface used for
// isEmpty/size) falls through with no verification support.

fun <!VIPER_TEXT!>setIsEmpty<!>(s: Set<Int>): Boolean = s.isEmpty()

fun <!VIPER_TEXT!>mutableSetAdd<!>(s: MutableSet<Int>, x: Int) {
    s.add(x)
}

fun <!VIPER_TEXT!>mutableSetContains<!>(s: MutableSet<Int>, x: Int): Boolean = s.contains(x)
