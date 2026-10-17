// FULL_JDK

// #258: Enum entries have no embedding.
// The plugin crashes when converting a function that references an enum type — it tries
// to embed the enum class and hits an unsupported symbol type for its entries.
// The INTERNAL_ERROR diagnostic lands on the enum class declaration (at the class name),
// not at the individual call sites, because that is where e.source points when
// attempting to embed the enum entry symbol whose dispatch receiver is the enum class.
// VIPER_TEXT appears for the auto-generated values() function that converts successfully.
// Multiple functions referencing the same enum produce multiple identical diagnostics
// at the same source position; these are deduplicated to one marker per position.

<!INTERNAL_ERROR!><!VIPER_TEXT!>enum class Direction<!> { North, South, East, West }<!>
<!INTERNAL_ERROR!><!VIPER_TEXT!>enum class Status<!> { Active, Inactive, Pending }<!>

// One representative function per enum type is sufficient to trigger the crash.
fun northDirection(): Direction = Direction.North

fun statusCode(s: Status): Int {
    return when (s) {
        Status.Active   -> 1
        Status.Inactive -> 0
        Status.Pending  -> -1
    }
}
