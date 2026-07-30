/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.core.diagnostics

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error1

/**
 * Diagnostics emitted by the conversion pipeline (FIR -> ExpEmbedding -> Viper).
 *
 * Kept in `core` so conversion code can call `reporter.reportOn(source, ConversionErrors.X, msg)`
 * directly instead of buffering through an intermediate collector.
 */
object ConversionErrors : KtDiagnosticsContainer() {
    val PURITY_VIOLATION by error1<PsiElement, String>()
    val MINOR_INTERNAL_ERROR by error1<PsiElement, String>(SourceElementPositioningStrategies.DECLARATION_NAME)

    /**
     * Per-function summary fired by `ProgramConverter.validateAll` whenever a registered declaration's
     * pipeline (signature embedding, body conversion, or validation) produced any blocking errors.
     */
    val VERIFICATION_SKIPPED by error1<PsiElement, String>(SourceElementPositioningStrategies.DECLARATION_NAME)

    /** A `predicate { }` block that does not form a well-shaped predicate declaration. */
    val MALFORMED_PREDICATE_DECLARATION by error1<PsiElement, String>(SourceElementPositioningStrategies.DECLARATION_NAME)

    /** A predicate declaration whose receiver is not a class the plugin has an embedding for. */
    val PREDICATE_WITHOUT_CLASS by error1<PsiElement, String>(SourceElementPositioningStrategies.DECLARATION_NAME)

    /** A call to a predicate declaration somewhere other than a specification block. */
    val PREDICATE_OUTSIDE_SPECIFICATION by error1<PsiElement, String>(SourceElementPositioningStrategies.DEFAULT)

    override fun getRendererFactory() = ConversionErrorMessages
}
