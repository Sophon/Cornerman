package io.github.sophon.fightingnerd.core.usecase

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.data.ReviewPolicyRepo
import io.github.sophon.fightingnerd.feat.review.platform.ReviewHandler
import io.github.sophon.fightingnerd.feat.review.SessionContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

internal class RequestReviewUseCase(
    private val reviewPolicyRepo: ReviewPolicyRepo,
    private val reviewHandler: ReviewHandler,
    private val appScope: CoroutineScope,
) {
    operator fun invoke(sessionContext: SessionContext) {
        appScope.launch {
            if (shouldTrigger(sessionContext).not()) return@launch

            Napier.d(tag = TAG) { "Review: triggering (${sessionContext::class.simpleName})" }
            reviewHandler.requestReview()
                .onSuccess {
                    Napier.d(tag = TAG) { "Review: Success" }
                }
                .onError { error ->
                    Napier.d(tag = TAG) { error.errorMessage }
                }
        }
    }

    private suspend fun shouldTrigger(sessionContext: SessionContext): Boolean {
        /**
         * 1. installation is at least one week old
         * 2. session at least 10s long
         */
        val isSessionLongEnough = (sessionContext.duration >= DURATION_SESSION)

        val isInstallationOldEnough = reviewPolicyRepo.getInstallationTimestamp().first()?.let { instant ->
            val age = (Clock.System.now() - instant)
            age >= DURATION_INSTALLATION
        } ?: false

        val shouldTrigger = isSessionLongEnough && isInstallationOldEnough
        if (shouldTrigger.not()) {
            Napier.d(tag = TAG) {
                "Review: skipped (${sessionContext::class.simpleName}) — session=$isSessionLongEnough, install=$isInstallationOldEnough"
            }
        }

        return shouldTrigger
    }


    private companion object {
        const val TAG = "RequestReviewUseCase"
        val DURATION_SESSION = 10.seconds
        val DURATION_INSTALLATION = 7.days
    }
}
