/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.embeddings.types

import org.jetbrains.kotlin.formver.core.conversion.TypeResolver
import org.jetbrains.kotlin.formver.core.domains.RuntimeTypeDomain
import org.jetbrains.kotlin.formver.core.names.PredicateName
import org.jetbrains.kotlin.formver.core.names.ScopedName
import org.jetbrains.kotlin.formver.core.names.asScope
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
     * Emit a user-declared predicate: the subject's type invariant and access to `C$unique`, conjoined
     * with the body the user wrote.
     *
     * Extending `C$unique` rather than replacing it keeps the permissions the rest of the plugin
     * relies on, so holding a custom predicate is always at least as strong as holding the class one.
     *
     * The subject's type invariant is conjoined directly, not left to `C$unique`, even though `C$unique`
     * asserts it too. A predicate access does not expose its body, so a `val` property read in the user
     * body — which embeds as a Viper function requiring `isSubtype(typeOf(subject), C())` — would have no
     * justification for that precondition. This is what makes a recursive predicate reach its own link.
     */
    context(ctx: TypeResolver)
    fun customPredicate(predicate: CustomPredicateEmbedding): Predicate =
        ClassPredicateBuilder.build(name, predicate.predicateName, predicate.subjectName) {
            includeSubTypeInvariants()
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
