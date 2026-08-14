// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

// #250: No Map/dictionary ADT model.
// Key finding: HashMap IS embedded via the generic class path, so construction and
// READ operations (containsKey, size) CONVERT to Viper. WRITE operations (m[k] = v,
// `set` via indexed assignment) crash with INTERNAL_ERROR — same root cause as #252
// (no MutableMap.set spec). The full fix requires both the class spec AND a finite-map
// domain (get/put axioms).

// Constructing a HashMap — no crash; the class gets a generic embedding.
@AlwaysVerify
fun <!VIPER_TEXT!>constructMap<!>(): Map<String, Int> {
    return HashMap<String, Int>()
}

// put (indexed write) crashes; the subsequent map read is never reached.
@AlwaysVerify
fun putAndGet(): Int {
    val m = HashMap<String, Int>()
    <!INTERNAL_ERROR!>m["key"] = 1<!>
    return m["key"] ?: 0
}

// containsKey — READ operation, converts successfully.
@AlwaysVerify
fun <!VIPER_TEXT!>containsKey<!>(m: Map<String, Int>, k: String): Boolean {
    return m.containsKey(k)
}

// sizeAfterPuts — crashes on the first map write.
@AlwaysVerify
fun sizeAfterPuts(): Int {
    val m = HashMap<String, Int>()
    <!INTERNAL_ERROR!>m["a"] = 1<!>
    m["b"] = 2
    m["c"] = 3
    return m.size
}

// Two-sum pattern: the write (seen[l[i]] = ...) crashes.
@AlwaysVerify
fun twoSumCount(l: List<Int>, target: Int): Int {
    val seen = HashMap<Int, Int>()
    var count = 0
    var i = 0
    while (i < l.size) {
        val complement = target - l[i]
        if (seen.containsKey(complement)) count++
        <!INTERNAL_ERROR!>seen[l[i]] = (seen[l[i]] ?: 0) + 1<!>
        i++
    }
    return count
}

// Frequency map: the write crashes.
@AlwaysVerify
fun frequencyMap(l: List<Int>): Map<Int, Int> {
    val freq = HashMap<Int, Int>()
    var i = 0
    while (i < l.size) {
        <!INTERNAL_ERROR!>freq[l[i]] = (freq[l[i]] ?: 0) + 1<!>
        i++
    }
    return freq
}

// MutableMap parameter: indexed write crashes.
@AlwaysVerify
fun updateMap(m: MutableMap<String, Int>, k: String, v: Int) {
    <!INTERNAL_ERROR!>m[k] = v<!>
}
