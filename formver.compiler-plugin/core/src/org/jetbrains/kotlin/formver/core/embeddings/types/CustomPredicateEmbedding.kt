/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.embeddings.types

import org.jetbrains.kotlin.formver.core.embeddings.expression.ExpEmbedding
import org.jetbrains.kotlin.formver.core.names.FreshName
import org.jetbrains.kotlin.formver.core.names.ScopedName
import org.jetbrains.kotlin.formver.viper.ast.PermExp

/**
 * A user-declared predicate over the state of a class, introduced by a `predicate { }` function body.
 *
 * The predicate is emitted as `C$name(this: Ref)` alongside the synthesised `C$unique`, and its body
 * conjoins access to `C$unique` with [body]. [body] is already expressed in terms of a variable named
 * [subjectName], because it was converted in the declaring function's own parameter scope, so it needs
 * no hole to fill.
 */
class CustomPredicateEmbedding(
    val className: ScopedName,
    val predicateName: ScopedName,
    val subjectName: FreshName,
) {
    /**
     * The converted user body. Assigned after construction rather than passed in: a recursive predicate
     * refers to itself, so the embedding has to be resolvable while its own body is still being converted.
     */
    lateinit var body: ExpEmbedding

    fun accessInvariant() = PredicateAccessTypeInvariantEmbedding(predicateName, PermExp.FullPerm())
}
