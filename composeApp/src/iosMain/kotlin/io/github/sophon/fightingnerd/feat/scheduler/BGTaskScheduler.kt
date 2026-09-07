package io.github.sophon.fightingnerd.feat.scheduler

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.Flow
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTaskScheduler as PlatformScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.dateWithTimeIntervalSinceNow
import kotlin.time.Duration

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class BGTaskScheduler(
    private val preferenceRepo: PreferenceRepo,
): Scheduler {

    override suspend fun setPeriod(duration: Duration): EmptyResult<AppError> {
        val result = try {
            val request = BGProcessingTaskRequest(identifier = TASK_IDENTIFIER)
            request.earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(
                duration.inWholeSeconds.toDouble()
            )
            val submitError: String? = memScoped {
                val errorRef = alloc<ObjCObjectVar<NSError?>>()
                PlatformScheduler.sharedScheduler.submitTaskRequest(request, errorRef.ptr).let { ok ->
                    if (ok) {
                        null
                    } else {
                        errorRef.value?.localizedDescription.orEmpty()
                    }
                }
            }

            if (submitError != null) {
                Result.Error(AppError.Unknown(submitError))
            } else {
                preferenceRepo.setUpdateInterval(duration)
            }
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }
        return result
    }

    override suspend fun cancel(): EmptyResult<AppError> {
        val result = try {
            PlatformScheduler.sharedScheduler.cancelTaskRequestWithIdentifier(TASK_IDENTIFIER)
            preferenceRepo.setUpdateInterval(null)
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }
        return result
    }

    override fun subscribeToPeriod(): Flow<Duration?> {
        val flow = preferenceRepo.subscribeToUpdateInterval()
        return flow
    }


    private companion object {
        const val TASK_IDENTIFIER = "io.github.sophon.fightingnerd.refresh"
    }
}
