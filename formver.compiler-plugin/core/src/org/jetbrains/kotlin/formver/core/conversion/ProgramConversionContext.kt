/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.conversion

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.formver.common.PluginConfiguration
import org.jetbrains.kotlin.formver.core.diagnostics.ErrorCollectionContext
import org.jetbrains.kotlin.formver.core.embeddings.callables.CallableEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.callables.NamedFunctionSignature
import org.jetbrains.kotlin.formver.core.embeddings.expression.AnonymousBuiltinVariableEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.expression.AnonymousVariableEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.expression.ExpEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.expression.VariableEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.properties.PropertyEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.types.FunctionTypeEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.types.TypeEmbedding
import org.jetbrains.kotlin.formver.core.names.CatchLabelName
import org.jetbrains.kotlin.formver.core.names.TryExitLabelName
import org.jetbrains.kotlin.formver.viper.NameResolver

interface ProgramConversionContext : ErrorCollectionContext {

    val config: PluginConfiguration

    val session: FirSession

    val whileIndexProducer: SimpleFreshEntityProducer<Int>
    val catchLabelNameProducer: SimpleFreshEntityProducer<CatchLabelName>
    val tryExitLabelNameProducer: SimpleFreshEntityProducer<TryExitLabelName>
    val scopeIndexProducer: SimpleFreshEntityProducer<ScopeIndex.Indexed>

    val anonVarProducer: FreshEntityProducer<AnonymousVariableEmbedding, TypeEmbedding>
    val anonBuiltinVarProducer: FreshEntityProducer<AnonymousBuiltinVariableEmbedding, TypeEmbedding>
    val returnTargetProducer: FreshEntityProducer<ReturnTarget, TypeEmbedding>
    val nameResolver: NameResolver
    val typeResolver: TypeResolver
    val convertedBodyResolver: ConvertedBodyResolver
    val linearizedBodyResolver: LinearizedBodyResolver

    /**
     * Whether conversion is currently inside a specification block: `preconditions { }`,
     * `postconditions { }`, `loopInvariants { }`, a `forAll { }` body, or a `predicate { }` body.
     *
     * Only meaningful for constructs that have no runtime meaning, such as a predicate access.
     */
    val inSpecification: Boolean

    /** Run [action] with [inSpecification] set; nests, so an inner block does not clear the flag. */
    fun <R> withinSpecification(action: () -> R): R

    /** Report a predicate access built where [inSpecification] is false. */
    fun reportPredicateOutsideSpecification(source: KtSourceElement?, msg: String)

    /** Report a specification block (`preconditions`, `postconditions`, `loopInvariants`) whose argument is not a lambda literal. */
    fun reportMalformedSpecificationBlock(source: KtSourceElement?, msg: String)

    fun embedAnyFunction(symbol: FirFunctionSymbol<*>): CallableEmbedding
    fun embedType(type: ConeKotlinType): TypeEmbedding
    fun embedFunctionPretype(symbol: FirFunctionSymbol<*>): FunctionTypeEmbedding
    fun embedType(exp: FirExpression): TypeEmbedding = embedType(exp.resolvedType)
    fun embedProperty(symbol: FirPropertySymbol): PropertyEmbedding
    fun embedContract(
        symbol: FirFunctionSymbol<*>, signature: NamedFunctionSignature, returnTarget: ReturnTarget
    ): Pair<List<ExpEmbedding>, List<ExpEmbedding>>

    /**
     * Returns true if the property has default behavior. That is:
     * It cannot be overwritten and does not have custom getters or setters
     */
    fun isGuaranteedDefaultProperty(symbol: FirPropertySymbol): Boolean
}

fun ProgramConversionContext.freshAnonVar(type: TypeEmbedding): VariableEmbedding = anonVarProducer.getFresh(type)
fun ProgramConversionContext.freshAnonBuiltinVar(type: TypeEmbedding): VariableEmbedding =
    anonBuiltinVarProducer.getFresh(type)
