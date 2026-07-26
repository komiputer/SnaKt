/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.embeddings.expression

import org.jetbrains.kotlin.formver.core.embeddings.ExpVisitor
import org.jetbrains.kotlin.formver.core.embeddings.types.TypeEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.types.buildType
import org.jetbrains.kotlin.formver.core.purity.PurityContext
import org.jetbrains.kotlin.formver.core.purity.isPure
import org.jetbrains.kotlin.formver.viper.ast.Exp

/**
 * Especially when working with type information, there are a number of expressions that do not have a corresponding `ExpEmbedding`.
 * We will eventually want to solve this somehow, but there are still open design questions there, so for now this wrapper will
 * do the job.
 */
data class ExpWrapper(val value: Exp, override val type: TypeEmbedding) : ExpEmbedding {

    override fun <R> accept(v: ExpVisitor<R>): R = v.visitExpWrapper(this)
}

data object ErrorExp : ExpEmbedding {
    override val type: TypeEmbedding = buildType { nothing() }

    override fun <R> accept(v: ExpVisitor<R>): R = v.visitErrorExp(this)
}

data class Assert(val exp: ExpEmbedding) : ExpEmbedding {
    override val type: TypeEmbedding = buildType { unit() }

    override fun children(): Sequence<ExpEmbedding> = sequenceOf(exp)
    override fun <R> accept(v: ExpVisitor<R>): R = v.visitAssert(this)

    override fun isValid(ctx: PurityContext): Boolean = exp.isPure().also {
        if (!it) ctx.addPurityError(exp, "Assert condition is impure")
    }
}

/**
 * Immediately performs an unconditional inhale of the statement.
 *
 * This can cause all kinds of issues with statement ordering, so it's more of a solution for porting legacy stuff than something
 * we should be adding more of going forward.
 */
data class InhaleDirect(val exp: ExpEmbedding) : ExpEmbedding {
    override val type: TypeEmbedding = buildType { unit() }

    override fun children(): Sequence<ExpEmbedding> = sequenceOf(exp)
    override fun <R> accept(v: ExpVisitor<R>): R = v.visitInhaleDirect(this)
    override fun isValid(ctx: PurityContext): Boolean = exp.isPure().also {
        if (!it) ctx.addPurityError(exp, "Inhale condition is impure")
    }
}

/**
 * Immediately performs an unconditional exhale of the statement.
 *
 * Mirrors [InhaleDirect]. Translates to Viper's `exhale`, which asserts [exp] and then transfers
 * it from the proof state. For pure boolean conditions (the only input class this feature supports),
 * Viper's `exhale` checks the condition but does not remove it from the path condition — only
 * permission-typed resources (`acc(...)`) are consumed by exhale. The translation is correct;
 * the consumption semantics are not observable for boolean-only inputs.
 */
data class ExhaleDirect(val exp: ExpEmbedding) : ExpEmbedding {
    override val type: TypeEmbedding = buildType { unit() }

    override fun children(): Sequence<ExpEmbedding> = sequenceOf(exp)
    override fun <R> accept(v: ExpVisitor<R>): R = v.visitExhaleDirect(this)
    override fun isValid(ctx: PurityContext): Boolean = exp.isPure().also {
        if (!it) ctx.addPurityError(exp, "Exhale condition is impure")
    }
}


data class Unfold(val pred: PredicateAccessPermissions) : ExpEmbedding {
    override val type: TypeEmbedding = buildType { unit() }

    override fun children(): Sequence<ExpEmbedding> = sequenceOf(pred)
    override fun <R> accept(v: ExpVisitor<R>): R = v.visitUnfold(this)
}

data class Fold(val pred: PredicateAccessPermissions) : ExpEmbedding {
    override val type: TypeEmbedding = buildType { unit() }

    override fun children(): Sequence<ExpEmbedding> = sequenceOf(pred)
    override fun <R> accept(v: ExpVisitor<R>): R = v.visitFold(this)
}
