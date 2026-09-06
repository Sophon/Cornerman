package io.github.sophon.fightingnerd.feat.scheduler

import kotlin.time.Duration

interface Scheduler {
    fun setPeriod(duration: Duration)
    fun cancel()
    fun getPeriod(): Duration?
}

