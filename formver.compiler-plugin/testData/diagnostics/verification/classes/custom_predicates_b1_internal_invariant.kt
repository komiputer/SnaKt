// FULL_JDK
// RENDER_PREDICATES

// Community case: https://youtrack.jetbrains.com/issue/25-1351430
// A class knows internally that `user` is non-null whenever `loggedIn` is true, but the public type
// of `user` has to stay `String?`. The predicate states the guarantee the type cannot.
// Both properties are `val`: the plugin has no `!!`, so the guarantee is reached by smart-casting
// off an immutable property.

import org.jetbrains.kotlin.formver.plugin.predicate
import org.jetbrains.kotlin.formver.plugin.preconditions

class Session(val loggedIn: Boolean, val user: String?)

fun Session.userPresentWhenLoggedIn(): Boolean = predicate {
    !loggedIn || user != null
}

fun greet(s: Session) {
    preconditions {
        s.userPresentWhenLoggedIn()
    }
}

fun greetTwice(s: Session) {
    preconditions {
        s.userPresentWhenLoggedIn()
    }
    greet(s)
    greet(s)
}
