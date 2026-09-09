package io.github.sophon.fightingnerd.feat.review.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.feat.review.ReviewHandler
import io.github.sophon.fightingnerd.feat.review.SessionContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class RequestReviewUseCase(
    private val reviewHandler: ReviewHandler,
    private val appScope: CoroutineScope,
) {
    operator fun invoke(sessionContext: SessionContext) {
        if (shouldTrigger(sessionContext.duration).not()) {
            Napier.d(tag = TAG) { "Review: skipped (${sessionContext::class.simpleName})" }
            return
        }

        appScope.launch {
            reviewHandler.requestReview()
                .onSuccess {
                    Napier.d(tag = TAG) { "Review: Success" }
                }
                .onError { error ->
                    Napier.d(tag = TAG) { error.errorMessage }
                }
        }
    }

    private fun shouldTrigger(sessionDuration: Duration): Boolean {
        /**
         * 1. installation is at least one week old
         * 2. session at least 10s long
         */
        val isSessionLongEnough = (sessionDuration >= DURATION_SESSION_S.seconds)

        return isSessionLongEnough
    }


    private companion object {
        const val TAG = "SummonReviewUseCase"
        const val DURATION_SESSION_S = 10
    }
}
