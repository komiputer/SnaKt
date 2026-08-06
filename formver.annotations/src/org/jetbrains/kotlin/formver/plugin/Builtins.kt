/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.plugin

private class FormverFunctionCalledInRuntimeException(offendingFunction: String) :
    RuntimeException("Function `$offendingFunction` should never be called in runtime.")

/**
 * Built-in function used to mark a boolean predicate to be verified in Viper.
 * This function hooks-in in the `formver` plugin, its invocation in a Kotlin
 * program does not do anything.
 */
fun verify(@Suppress("UNUSED_PARAMETER") vararg predicates: Boolean) = Unit

infix fun Boolean.implies(other: Boolean) = !this || other

fun loopInvariants(@Suppress("UNUSED_PARAMETER") body: () -> Unit) = Unit

fun preconditions(@Suppress("UNUSED_PARAMETER") body: () -> Unit) = Unit

fun <T> postconditions(@Suppress("UNUSED_PARAMETER") body: (T) -> Unit) = Unit


fun <T> forAll(@Suppress("UNUSED_PARAMETER") body: InvariantBuilder.(T) -> Unit): Boolean =
    throw FormverFunctionCalledInRuntimeException("forAll")


fun <T> old(@Suppress("UNUSED_PARAMETER") body: T): T =
    throw FormverFunctionCalledInRuntimeException("old")


/**
 * Declares a predicate over the state of the receiver's class.
 *
 * The enclosing function must return [Boolean] and have `predicate { }` as its entire body; its
 * name becomes the predicate's name and its receiver becomes the predicate's subject. The block
 * body is an implicit conjunction, and it holds in addition to the permissions the class always
 * carries, so a predicate strengthens the class invariant rather than replacing it.
 *
 * The [Boolean] return type is what lets a predicate refer to itself: `next!!.sorted()` composes
 * with `&&` and `||` inside its own body.
 *
 * ```kotlin
 * fun Node.sorted(): Boolean = predicate {
 *     next == null || (value <= next!!.value && next!!.sorted())
 * }
 * ```
 *
 * A predicate may only be named in a specification: `preconditions { }`, `postconditions { }`
 * or `loopInvariants { }`.
 */
fun predicate(@Suppress("UNUSED_PARAMETER") body: () -> Unit): Boolean =
    throw FormverFunctionCalledInRuntimeException("predicate")


/**
 * Requests access permission to the field denoted by [path] in a pre- or postcondition.
 *
 * [path] must be a field access such as `x.a`. The optional [permission] selects how much
 * permission is requested; use [write] for full (the default) or [read] for a read-only
 * (wildcard) fraction.
 */
fun acc(
    @Suppress("UNUSED_PARAMETER") path: Any?,
    @Suppress("UNUSED_PARAMETER") permission: Permission? = null
): Boolean =
    throw FormverFunctionCalledInRuntimeException("acc")

/**
 * An amount of permission to a location, such as full ([write]) or read-only ([read]).
 */
interface Permission

/**
 * A verification predicate over [exp].
 */
abstract class Predicate(val exp: Any)

/**
 * Denotes a read-only (wildcard) permission amount. Only meaningful as the second argument of [acc].
 */
fun read(): Permission =
    throw FormverFunctionCalledInRuntimeException("read")

/**
 * Denotes a full (write) permission amount. Only meaningful as the second argument of [acc].
 */
fun write(): Permission =
    throw FormverFunctionCalledInRuntimeException("write")

/**
 * The uniqueness predicate of [data]: exclusive access to [data] and its fields.
 */
data class UniquePred(val data: Any) : Predicate(data)

/**
 * Exchanges [exp] for access to its body, exposing the underlying fields. The inverse of [fold].
 *
 * [permission] is the amount of the predicate to unfold, defaulting to full ([write]).
 */
fun unfold(
    @Suppress("UNUSED_PARAMETER") exp: Predicate,
    @Suppress("UNUSED_PARAMETER") permission: Permission? = null
): Unit =
    throw FormverFunctionCalledInRuntimeException("unfold")

/**
 * Exchanges access to [exp]'s body for the predicate itself. The inverse of [unfold].
 *
 * [permission] is the amount of the predicate to fold, defaulting to full ([write]).
 */
fun fold(
    @Suppress("UNUSED_PARAMETER") exp: Predicate,
    @Suppress("UNUSED_PARAMETER") permission: Permission? = null
): Unit = throw FormverFunctionCalledInRuntimeException("fold")

class InvariantBuilder {
    /**
     * Specifies trigger expressions for quantifiers.
     * This function should be called within a `forAll` block to provide user-defined triggers
     * for SMT solver guidance.
     */
    fun triggers(@Suppress("UNUSED_PARAMETER") vararg expressions: Any?): Unit =
        throw FormverFunctionCalledInRuntimeException("triggers")
}
