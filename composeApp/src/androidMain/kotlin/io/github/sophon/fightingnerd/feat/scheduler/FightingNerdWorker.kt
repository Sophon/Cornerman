package io.github.sophon.fightingnerd.feat.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result as WorkResult
import androidx.work.WorkerParameters
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.core.usecase.RefreshUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

internal class FightingNerdRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {
    private val refreshUseCase: RefreshUseCase by inject()
    private val preferenceRepo: PreferenceRepo by inject()

    override suspend fun doWork(): WorkResult {
        val interval = preferenceRepo.subscribeToUpdateInterval().first()
        if (interval == null) {
            Napier.i(tag = TAG) { "doWork: no interval configured, skipping" }
            return WorkResult.success()
        }

        Napier.i(tag = TAG) { "doWork: refreshing (olderThan $interval)" }
        refreshUseCase(olderThan = interval).collect { emission ->
            emission
                .onSuccess { report -> Napier.i(tag = TAG) { "doWork: $report" } }
                .onError { error -> Napier.e(tag = TAG) { "doWork: $error" } }
        }

        return WorkResult.success()
    }


    private companion object {
        const val TAG = "Scheduler"
    }
}

