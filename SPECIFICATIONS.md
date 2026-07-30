# Writing Specifications in SnaKt

SnaKt translates Kotlin code with formal specifications to [Viper](https://www.pm.inf.ethz.ch/research/viper.html) for verification. This guide assumes familiarity with Hoare logic; see the [Viper tutorial](http://viper.ethz.ch/tutorial/) if needed.

## Verification Control

By default, SnaKt only verifies functions with Kotlin `contract { }` blocks. To verify functions with SnaKt specifications:

```kotlin
import org.jetbrains.kotlin.formver.plugin.*

@AlwaysVerify  // Enables verification for this function
fun divide(numerator: Int, denominator: Int): Int {
    preconditions { denominator != 0 }
    return numerator / denominator
}
```

**Annotations:**
- `@AlwaysVerify` — verify this function regardless of plugin settings
- `@NeverVerify` — skip verification even with contracts
- `@NeverConvert` — skip Viper conversion entirely

**Plugin configuration** (in `build.gradle.kts`):
```kotlin
formver {
    verificationTargetsSelection("all_targets")  // Verify all functions
    // or "targets_with_contract" (default) — only Kotlin contract { } blocks
    // or "no_targets" — disable verification
}
```

Note that `@AlwaysVerify` overrides plugin settings.

## Preconditions and Postconditions

```kotlin
@AlwaysVerify
fun abs(x: Int): Int {
    postconditions<Int> { result ->
        result >= 0
        result == x || result == -x
    }
    return if (x >= 0) x else -x
}
```

Multiple conditions are implicitly conjoined. The postconditions block receives the return value as its parameter.

## Loop Invariants

```kotlin
@AlwaysVerify
fun sumUpTo(n: Int): Int {
    preconditions { n >= 0 }
    var sum = 0
    var i = 0
    while (i <= n) {
        loopInvariants {
            i >= 0
            sum == i * (i - 1) / 2
        }
        sum += i
        i++
    }
    return sum
}
```

The rules are as follows:
- Loop invariant must hold when the loop is entered.
- The loop body may assume the condition holds.
- Loop invariant must hold after each iteration.
- Loop invariant must hold when the loop is exited.
- Code after the loop may assume the condition fails.

## Universal Quantification

Use `forAll<T>` for quantified formulas:

```kotlin
@AlwaysVerify
fun example(arr: IntArray): Unit {
    preconditions {
        forAll<Int> { j ->
            (0 <= j && j < arr.size()) implies (arr[j] > 0)
        }
    }
    // ...
}
```

The `implies` infix operator is provided for convenience (`a implies b` ≡ `!a || b`).

### Triggers

By default, Viper infers triggers automatically. You can specify them explicitly:

```kotlin
forAll<Int> { x ->
    triggers(x * x)  // Single trigger
    x * x >= 0
}

forAll<Int> { x ->
    triggers(x * x, x + 1)  // Multiple triggers
    x != 0 implies (x * x > 0)
}
```

Each argument to `triggers()` becomes a separate trigger. This differs from Viper syntax where you can group multiple expressions in a single trigger; currently SnaKt only supports simple (single-expression) triggers.

## Custom Predicates

A function whose entire body is `predicate { }` declares a Viper predicate over the state of its receiver. The function's name becomes the predicate's name, and its receiver becomes the predicate's subject. The block body is an implicit conjunction; it holds *in addition* to the permissions the class always carries, so a predicate strengthens the class invariant rather than replacing it.

```kotlin
class Interval(var lo: Int, var hi: Int)

fun Interval.ordered(): Boolean = predicate {
    lo <= hi
}

@AlwaysVerify
fun useOrdered(i: Interval) {
    preconditions {
        i.ordered()
    }
    verify(i.lo <= i.hi)
}
```

The `Boolean` return type is what lets a predicate refer to itself recursively:

```kotlin
class Node(var value: Int, val next: Node?)

fun Node.sorted(): Boolean = predicate {
    next == null || (value <= next.value && next.sorted())
}

@AlwaysVerify
fun useSorted(n: Node) {
    preconditions {
        n.sorted()
    }
}
```

`ordered` generates the following Viper predicate:

```viper
predicate ordered(v_this_extension: Ref) {
  acc(Interval_unique(v_this_extension), write) &&
  intFromRef((unfolding acc(Interval_unique(v_this_extension), write) in
    v_this_extension.lo)) <=
  intFromRef((unfolding acc(Interval_unique(v_this_extension), write) in
    v_this_extension.hi))
}
```

and `sorted`, recursively:

```viper
predicate sorted(v_this_extension: Ref) {
  acc(Node_unique(v_this_extension), write) &&
  (next(v_this_extension) == nullValue() ||
  intFromRef((unfolding acc(Node_unique(v_this_extension), write) in
    v_this_extension.value)) <=
  intFromRef((unfolding acc(Node_unique(next(v_this_extension)), write) in
    next(v_this_extension).value)) &&
  acc(sorted(next(v_this_extension)), write))
}
```

A predicate may only be named inside `preconditions { }`, `postconditions { }`, `loopInvariants { }`, a `forAll { }` body or another predicate's body; naming one anywhere else is an error, and calling one at runtime throws.

Reading a field inside a specification needs no annotation: the necessary `unfolding` is inserted automatically, including for a recursive predicate.

**Known limitation:** the plugin does not support `!!`. A recursive predicate that needs to smart-cast a nullable link must expose it through a `val` (as `next` above), not a `var`, so the compiler can smart-cast it instead of requiring `!!`.

**Known limitation:** a predicate constrains what a *specification* may say, not what a method body may read. Holding `acc(P(x))` in a method does not let that body read `x`'s `var` fields, because the plugin replaces every `var` field read in a method body with `havoc` regardless of the permissions held — the same is true of the class predicate `C$unique`, so this is not specific to custom predicates. `val` properties embed as permission-free functions and can be read normally.

## Additional Plugin Options

```kotlin
formver {
    errorStyle("user_friendly")  // or "original_viper", "both"
    logLevel("only_warnings")    // or "short_viper_dump", "full_viper_dump"
    unsupportedFeatureBehaviour("throw_exception")  // or "assume_unreachable"
}
```
