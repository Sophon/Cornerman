package io.github.sophon.fightingnerd.feat.review

import kotlin.time.Duration

internal sealed interface SessionContext {
    val duration: Duration

    data class MoveList(override val duration: Duration = Duration.ZERO) : SessionContext
}
