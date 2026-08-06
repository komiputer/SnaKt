/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.conversion

import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.formver.core.isFormverFunctionNamed

/**
 * Extracts the lambda argument of a call to a formver builtin recognised by [predicate], or `null`
 * if [this] is not such a call. If it is such a call but the argument is not a lambda literal (e.g.
 * a stored function value or a callable reference), [onMalformedArgument] is invoked with the call's
 * source and `null` is returned rather than throwing: callers can pass legal Kotlin here, and a
 * misuse must be reported as a diagnostic, not crash the compiler frontend.
 */
fun FirStatement.extractFormverFirBlock(
    onMalformedArgument: (FirFunctionCall) -> Unit = {},
    predicate: FirFunctionSymbol<*>.() -> Boolean,
): FirAnonymousFunction? {
    if (this !is FirFunctionCall) return null
    val firFunction = toResolvedCallableSymbol() as? FirFunctionSymbol<*> ?: return null
    if (!predicate(firFunction)) return null
    val formverInvariantsArgument = argument
    if (formverInvariantsArgument !is FirAnonymousFunctionExpression) {
        onMalformedArgument(this)
        return null
    }
    return formverInvariantsArgument.anonymousFunction
}

fun extractLoopInvariants(parentBlock: FirBlock, onMalformedArgument: (FirFunctionCall) -> Unit = {}): FirBlock? {
    val firstStmt = parentBlock.statements.firstOrNull() ?: return null
    return firstStmt.extractFormverFirBlock(onMalformedArgument) { isFormverFunctionNamed("loopInvariants") }?.body
}

/**
 * The `predicate { }` block of a predicate declaration, or `null` if [statement] is not such a call.
 */
fun FirStatement.extractPredicateBlock(): FirBlock? =
    unwrapReturn().extractFormverFirBlock { isFormverFunctionNamed("predicate") }?.body

/**
 * The `predicate { }` block of [declaration] if it is a predicate declaration: a `Boolean`-returning
 * function whose entire body is a single `predicate { }` call.
 *
 * The return type is part of the pattern rather than something the caller checks afterwards, so that
 * a `predicate { }` block in a function returning anything else stays unrecognised and is reported as
 * a misuse instead.
 */
fun FirSimpleFunction.extractPredicateDeclarationBlock(): FirBlock? {
    if (!symbol.resolvedReturnType.isBoolean) return null
    val onlyStatement = body?.statements?.singleOrNull() ?: return null
    return onlyStatement.extractPredicateBlock()
}

/**
 * Whether [declaration] mentions `predicate(...)` anywhere in its body, regardless of whether its
 * argument is a lambda literal. Used to tell a malformed predicate declaration (including one whose
 * argument is not a lambda literal) apart from a function that simply has nothing to do with
 * predicates.
 */
fun FirSimpleFunction.mentionsPredicateBuiltin(): Boolean =
    body?.statements?.any { stmt ->
        val call = stmt.unwrapReturn() as? FirFunctionCall ?: return@any false
        (call.toResolvedCallableSymbol() as? FirFunctionSymbol<*>)?.isFormverFunctionNamed("predicate") == true
    } == true

private fun FirStatement.unwrapReturn(): FirStatement =
    (this as? FirReturnExpression)?.result ?: this

data class FirSpecification(val precond: FirBlock?, val postcond: FirBlock?, val returnVar: FirValueParameterSymbol?) {
    constructor() : this(null, null, null)
}

private fun FirAnonymousFunction.extractFormverReturnVar(returnType: ConeKotlinType): FirValueParameterSymbol {
    val param = valueParameters.first()
    if (param.symbol.resolvedReturnType != returnType)
        error("Expected type ${returnType} based on signature, got ${param.symbol.resolvedReturnType}")
    return param.symbol
}

fun extractFirSpecification(
    parentBlock: FirBlock,
    returnType: ConeKotlinType,
    onMalformedArgument: (FirFunctionCall) -> Unit = {},
): FirSpecification {
    val firstStmt = parentBlock.statements.firstOrNull() ?: return FirSpecification()

    firstStmt.extractFormverFirBlock(onMalformedArgument) { isFormverFunctionNamed("postconditions") }?.let { lambda ->
        return FirSpecification(null, lambda.body, lambda.extractFormverReturnVar(returnType))
    }

    val precond = firstStmt.extractFormverFirBlock(onMalformedArgument) { isFormverFunctionNamed("preconditions") }
        ?: return FirSpecification()
    val postcond =
        parentBlock.statements.getOrNull(1)
            ?.extractFormverFirBlock(onMalformedArgument) { isFormverFunctionNamed("postconditions") }
    return FirSpecification(precond.body, postcond?.body, postcond?.extractFormverReturnVar(returnType))
}
