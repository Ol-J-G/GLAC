package de.oljg.glac.alarms.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration


@RequiresApi(Build.VERSION_CODES.O)
fun isValidAlarmStart(localDate: LocalDate?, localTime: LocalTime?, lightAlarmDuration: Duration) =
        localDate != null && localTime != null
                && LocalDateTime.of(localDate, localTime).isInFuture(lightAlarmDuration)

/**
 *
 * ___now___<___lightAlarmTime___<___alarmTime___
 *     |              |                  |
 *                    |< 'sunrise' anim >|< alarmSound
 *
 * Note: Is's accurate to the minute (imho not useful to allow alarm start times lower than
 * minutes, at least in order of this app's purpose...)
 */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.isInFuture(lightAlarmDuration: Duration): Boolean {
    val now = LocalDateTime.now()
    val alarmTime = this
    val lightAlarmTime = alarmTime.minus(lightAlarmDuration)
    return LocalDateTime.of(now.year, now.month, now.dayOfMonth, now.hour, now.minute)
        .isBefore(lightAlarmTime)
}

/**
 * Use [kotlin.time.Duration] together with [java.time.LocalDateTime] to
 * substract a duration from a local date time object.
 * (better for now to use [kotlin.time.Duration.toJavaDuration] just once, here in this fun ...
 * (sadly, kotlin time lib is still experimental (today=2024-05-21), so relying on Java here :>))
 */
@RequiresApi(Build.VERSION_CODES.O)
operator fun LocalDateTime.minus(amountToSubstract: Duration): LocalDateTime =
        minus(amountToSubstract.toJavaDuration())


fun String.isIntIn(range: IntRange) = when {
    !this.isInt() -> false
    else -> this.toInt() in range
}

fun String.isInt() = try {
    this.toInt()
    true
} catch (e: NumberFormatException) {
    false
}

object Defaults {
    val MIN_LIGHT_ALARM_DURATION = 1.minutes
    val MAX_LIGHT_ALARM_DURATION = 60.minutes
}
