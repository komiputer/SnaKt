/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.embeddings.callables

import org.jetbrains.kotlin.formver.core.conversion.StmtConversionContext
import org.jetbrains.kotlin.formver.core.embeddings.expression.ExpEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.expression.PredicateAccessPermissions
import org.jetbrains.kotlin.formver.core.embeddings.types.CustomPredicateEmbedding
import org.jetbrains.kotlin.formver.core.embeddings.types.FunctionTypeEmbedding
import org.jetbrains.kotlin.formver.viper.ast.PermExp

/**
 * The callable behind a predicate declaration. Calling it does not invoke anything: `n.sorted()`
 * embeds as access to the predicate `Node$sorted(n)`, which is why such a call is only meaningful
 * inside a specification.
 */
class CustomPredicateCallable(
    override val callableType: FunctionTypeEmbedding,
    val predicate: CustomPredicateEmbedding,
) : CallableEmbedding {
    override fun insertCall(args: List<ExpEmbedding>, ctx: StmtConversionContext): ExpEmbedding =
        PredicateAccessPermissions(predicate.predicateName, listOf(args.first()), PermExp.FullPerm())
}
