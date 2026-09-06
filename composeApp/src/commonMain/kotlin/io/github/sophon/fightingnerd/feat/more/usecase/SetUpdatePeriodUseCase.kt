package io.github.sophon.fightingnerd.feat.more.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.scheduler.Scheduler
import kotlin.time.Duration

internal class SetUpdatePeriodUseCase(
    private val scheduler: Scheduler,
) {
    suspend operator fun invoke(period: Duration?): EmptyResult<AppError> {
        val result = if (period == null) {
            scheduler.cancel()
        } else {
            scheduler.setPeriod(period)
        }
        return result
    }
}
