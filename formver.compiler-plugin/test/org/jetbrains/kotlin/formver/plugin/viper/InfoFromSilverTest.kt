/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.formver.plugin.viper

import org.jetbrains.kotlin.formver.viper.ast.Info
import org.junit.jupiter.api.Test
import kotlin.test.assertSame
import viper.silver.ast.`NoInfo$`
import viper.silver.ast.`Synthesized$`

// Silicon attaches its own info kinds to the nodes it reports errors on. Those
// carry no SnaKt source role, and reading one back must yield an absent info
// rather than throwing: an error on such a node has to reach the user as an
// ordinary verification error.
class InfoFromSilverTest {
    @Test
    fun noInfoRoundTrips() {
        assertSame(Info.NoInfo, Info.fromSilver(`NoInfo$`.`MODULE$`))
    }

    @Test
    fun foreignSilverInfoBecomesNoInfo() {
        assertSame(Info.NoInfo, Info.fromSilver(`Synthesized$`.`MODULE$`))
    }

    @Test
    fun silverCommentInfoBecomesNoInfo() {
        assertSame(Info.NoInfo, Info.fromSilver(viper.silver.ast.SimpleInfo(listOf("a comment").toScalaSeq())))
    }
}

private fun List<String>.toScalaSeq(): scala.collection.immutable.Seq<String> =
    scala.collection.JavaConverters.asScala(this).toSeq()
