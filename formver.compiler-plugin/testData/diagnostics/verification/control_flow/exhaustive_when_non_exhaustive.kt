// FULL_JDK

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

sealed interface Shape
class Sq(val side: Int) : Shape
class Rect(val width: Int, val height: Int) : Shape
class Circ(val radius: Int) : Shape

// `Circ` is not covered and there is no `else`, so the `when` is genuinely non-exhaustive. Kotlin's
// own exhaustiveness diagnostic must reject this before the plugin's lowering runs.
<!VIPER_VERIFICATION_ERROR!>@AlwaysVerify
fun <!VIPER_TEXT!>area<!>(s: Shape): Int = <!NO_ELSE_IN_WHEN!>when<!> (s) {
    is Sq -> s.side * s.side
    is Rect -> s.width * s.height
}<!>
