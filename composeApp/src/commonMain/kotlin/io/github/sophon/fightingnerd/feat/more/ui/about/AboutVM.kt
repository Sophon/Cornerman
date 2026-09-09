package io.github.sophon.fightingnerd.feat.more.ui.about

import androidx.lifecycle.ViewModel
import io.github.sophon.fightingnerd.core.usecase.OpenUrlUseCase
import io.github.sophon.fightingnerd.core.usecase.RequestReviewUseCase
import io.github.sophon.fightingnerd.core.util.ScreenStopWatch
import io.github.sophon.fightingnerd.feat.review.SessionContext

internal class AboutVM(
    private val openUrlUseCase: OpenUrlUseCase,
    private val requestReviewUseCase: RequestReviewUseCase,
) : ViewModel() {
    private val screenStopWatch = ScreenStopWatch()

    fun openUrl(url: String) {
        openUrlUseCase(url)
    }

    fun onScreenExit() {
        val sessionDuration = screenStopWatch.elapsed()
        val sessionContext = SessionContext.About(duration = sessionDuration)
        requestReviewUseCase(sessionContext)
    }
}
