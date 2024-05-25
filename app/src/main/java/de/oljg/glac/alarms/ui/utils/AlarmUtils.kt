package de.oljg.glac.alarms.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toJavaDuration


@RequiresApi(Build.VERSION_CODES.O)
fun isValidAlarmStart(date: LocalDate?, time: LocalTime?, lightAlarmDuration: Duration) =
        date != null && time != null
                && LocalDateTime.of(date, time).isInFuture(lightAlarmDuration)

/**
 * Considers lightAlarmDuration (when > 0) to ensure a user cannot choose an alarm start time, that
 * is before this users current time / 'now'.
 * First possible time is now + max. 1 minute (accurate to the minute) to lightAlarmTime or
 * alarmTime.
 *
 * Note: Is's accurate to the minute (imho not useful to allow alarm start times lower than
 * minutes, at least in order of this app's purpose...)
 *
 * Maybe the following "illustration" helps
 *
 * ___now____<____lightAlarmTime____<____alarmTime____________
 *     |               |                      |
 *  isBefore           |< lightAlarmDuration >|
 *     |<    5min     >|                      |
 *                     |<   'sunrise' anim   >|< alarmSound
 */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.isInFuture(lightAlarmDuration: Duration): Boolean {
    val now = LocalDateTime.now()
    val alarmTime = this.minus(5.minutes)
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


@RequiresApi(Build.VERSION_CODES.O)
operator fun LocalDateTime.plus(amountToAdd: Duration): LocalDateTime =
        plus(amountToAdd.toJavaDuration())


@RequiresApi(Build.VERSION_CODES.O)
operator fun LocalTime.plus(amountToAdd: Duration): LocalTime =
        plus(amountToAdd.toJavaDuration())


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

object AlarmDefaults {
    val MIN_LIGHT_ALARM_DURATION = 1.minutes
    val MAX_LIGHT_ALARM_DURATION = 60.minutes

    // Users can schedule an alarm from now + ALARM_START_BUFFER
    val ALARM_START_BUFFER = 5.minutes

    val minutesSaver: Saver<Duration, Any> = run {
        val minutes = "minutes"
        mapSaver(
            save = { duration -> mapOf(minutes to duration.toInt(DurationUnit.MINUTES)) },
            restore = { minutesMap -> (minutesMap[minutes] as Int).minutes }
        )
    }
}
