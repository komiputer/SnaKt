// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.verify

// #251: HashSet/HashMap have no stdlib collection spec, unlike MutableList.
// Key finding: HashSet IS embedded (ConeClassLikeType -> embedClass path), so
// construction and method calls CONVERT to Viper successfully. The bug is that
// add/contains/remove have NO hand-authored postconditions, so the converted Viper
// methods have no useful spec — verification of postconditions about set membership fails.
// Exception: removeFromSet with verify(!s.contains(x)) may fail to convert (see below).

// Constructing a HashSet — no crash, the class is embedded generically.
fun <!VIPER_TEXT!>constructSet<!>(): Set<Int> {
    return HashSet<Int>()
}

// Adding an element converts — but the postcondition "x is now in s" is absent.
fun <!VIPER_TEXT!>addToSet<!>(s: MutableSet<Int>, x: Int) {
    s.add(x)
}

// containsAfterAdd converts — but Viper can't prove the result is true.
fun <!VIPER_TEXT!>containsAfterAdd<!>(x: Int): Boolean {
    val s = HashSet<Int>()
    s.add(x)
    return s.contains(x)
}

// Size tracking converts — but the solver can't prove size == number of distinct adds.
fun <!VIPER_TEXT!>sizeAfterDistinctAdds<!>(): Int {
    val s = HashSet<Int>()
    s.add(1)
    s.add(2)
    s.add(3)
    return s.size
}

// Idiomatic duplicate-detection pattern — converts, but membership reasoning is opaque.
fun <!VIPER_TEXT!>firstRecurring<!>(l: List<Int>): Int {
    val seen = HashSet<Int>()
    var i = 0
    while (i < l.size) {
        if (seen.contains(l[i])) return l[i]
        seen.add(l[i])
        i++
    }
    return -1
}

// Unique character count — converts, but chars.size >= 0 is unproven without a spec.
fun <!VIPER_TEXT!>uniqueChars<!>(s: String): Int {
    val chars = HashSet<Char>()
    var i = 0
    while (i < s.length) {
        chars.add(s[i])
        i++
    }
    return chars.size
}

// Remove: verify(!s.contains(x)) — the postcondition has no spec to discharge it.
// This function converts without crash; the verify call fails during Viper verification.
fun removeFromSet(s: MutableSet<Int>, x: Int) {
    s.remove(x)
    verify(!s.contains(x))
}
