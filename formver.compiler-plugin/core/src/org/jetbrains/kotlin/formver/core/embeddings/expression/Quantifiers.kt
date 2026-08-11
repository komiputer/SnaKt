/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.embeddings.expression

import org.jetbrains.kotlin.formver.core.embeddings.ExpVisitor
import org.jetbrains.kotlin.formver.core.embeddings.types.TypeEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.types.buildType
import org.jetbrains.kotlin.formver.core.purity.PurityContext
import org.jetbrains.kotlin.formver.core.purity.isPure

/**
 * Viper's quantifiers only admit a pure body; an impure condition (a side effect, an impure call,
 * or a construct such as `List.get` that embeds as a method call) would otherwise only be caught
 * much later, as a linearizer crash rather than a diagnostic.
 */
private fun quantifierConditionsValid(conditions: List<ExpEmbedding>, ctx: PurityContext): Boolean {
    var valid = true
    conditions.forEach { condition ->
        if (!condition.isPure()) {
            valid = false
            ctx.addPurityError(condition, "Quantifier body must be pure")
        }
    }
    return valid
}

data class ForAllEmbedding(
    val variable: VariableEmbedding,
    val conditions: List<ExpEmbedding>,
    val triggerExpressions: List<ExpEmbedding> = emptyList(),
) : ExpEmbedding {

    override val type: TypeEmbedding
        get() = buildType { boolean() }

    override fun children(): Sequence<ExpEmbedding> = conditions.asSequence()
    override fun <R> accept(v: ExpVisitor<R>): R = v.visitForAllEmbedding(this)
    override fun isValid(ctx: PurityContext): Boolean = quantifierConditionsValid(conditions, ctx)
}

data class ExistsEmbedding(
    val variable: VariableEmbedding,
    val conditions: List<ExpEmbedding>,
    val triggerExpressions: List<ExpEmbedding> = emptyList(),
) : ExpEmbedding {

    override val type: TypeEmbedding
        get() = buildType { boolean() }

    override fun children(): Sequence<ExpEmbedding> = conditions.asSequence()
    override fun <R> accept(v: ExpVisitor<R>): R = v.visitExistsEmbedding(this)
    override fun isValid(ctx: PurityContext): Boolean = quantifierConditionsValid(conditions, ctx)
}
