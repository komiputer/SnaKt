/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.embeddings.types

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.formver.core.conversion.TypeResolver
import org.jetbrains.kotlin.formver.core.domains.RuntimeTypeDomain
import org.jetbrains.kotlin.formver.core.embeddings.expression.ExpEmbedding
import org.jetbrains.kotlin.formver.core.linearization.pureToViper
import org.jetbrains.kotlin.formver.core.names.PredicateName
import org.jetbrains.kotlin.formver.core.names.ScopedName
import org.jetbrains.kotlin.formver.core.names.asScope
import org.jetbrains.kotlin.formver.core.names.debugMangled
import org.jetbrains.kotlin.formver.viper.ast.DomainFunc
import org.jetbrains.kotlin.formver.viper.ast.Exp
import org.jetbrains.kotlin.formver.viper.ast.PermExp
import org.jetbrains.kotlin.formver.viper.ast.Predicate

// TODO: incorporate generic parameters.
data class ClassTypeEmbedding(override val name: ScopedName) : PretypeEmbedding {

    override val runtimeType: Exp = this.embedClassTypeFunc()()

    val uniquePredicateName = ScopedName(name.asScope(), PredicateName("unique"))

    context(ctx: TypeResolver)
    fun uniquePredicate(): Predicate = ClassPredicateBuilder.build(name, uniquePredicateName) {
        includeSubTypeInvariants()
        forEachPropertyField {
            forBackingField {
                if (!isAlwaysWriteable) {
                    addAccessPermissions(PermExp.FullPerm())

                    forType {
                        includeSubTypeInvariants()
                    }
                }
            }
            forType {
                if (isUnique) {
                    addAccessToUniquePredicate()
                }
            }
        }
        forEachSuperType {
            addAccessToUniquePredicate()
        }
    }

    /**
     * Emit a user-declared predicate: access to `C$unique` conjoined with the body the user wrote.
     *
     * Extending `C$unique` rather than replacing it keeps the permissions the rest of the plugin
     * relies on, so holding a custom predicate is always at least as strong as holding the class one.
     */
    context(ctx: TypeResolver)
    fun customPredicate(predicate: CustomPredicateEmbedding): Predicate =
        ClassPredicateBuilder.build(name, predicate.predicateName, predicate.subjectName) {
            includeOwnUniquePredicateAccess()
            addUserBody(predicate.body)
        }

    override fun accessInvariants(ctx: TypeResolver): List<TypeInvariantEmbedding> =
        ctx.flatMapUniqueFields(name) { field ->
            field.accessInvariantsForParameter()
        }

    override fun uniquePredicateAccessInvariant(ctx: TypeResolver) =
        PredicateAccessTypeInvariantEmbedding(uniquePredicateName, PermExp.FullPerm())

}


fun ClassTypeEmbedding.embedClassTypeFunc(): DomainFunc = RuntimeTypeDomain.classTypeFunc(name)

fun ClassTypeEmbedding.predicateAccess(
    receiver: ExpEmbedding, ctx: TypeResolver, source: KtSourceElement?
): Exp.PredicateAccess {
    val access = (uniquePredicateAccessInvariant(ctx).fillHole(receiver)
        .pureToViper(toBuiltin = true, ctx, source) as? Exp.PredicateAccess
        ?: error("Translating shared predicate of ${name.debugMangled} yielded no predicate access."))
    return access
}
