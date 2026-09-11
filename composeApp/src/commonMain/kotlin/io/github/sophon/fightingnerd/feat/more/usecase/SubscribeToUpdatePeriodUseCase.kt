package io.github.sophon.fightingnerd.feat.more.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.feat.scheduler.Scheduler
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

@ExcludeFromCoverage("UI")
internal class SubscribeToUpdatePeriodUseCase(
    private val scheduler: Scheduler,
) {
    operator fun invoke(): Flow<Duration?> {
        return scheduler.subscribeToPeriod()
    }
}
