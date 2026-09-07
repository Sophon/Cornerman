package io.github.sophon.core.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
fun Instant.toFormattedString(): String {
    return this
        .toLocalDateTime(TimeZone.UTC)
        .toString()
        .replace('T', ' ')
        .substringBefore('.')
}

fun Instant.toHumanReadableString(): String {
    val local = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = local.year.toString().padStart(4, '0')
    val month = local.monthNumber.toString().padStart(2, '0')
    val day = local.dayOfMonth.toString().padStart(2, '0')
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    val formatted = "$year-$month-$day $hour:$minute"
    return formatted
}
