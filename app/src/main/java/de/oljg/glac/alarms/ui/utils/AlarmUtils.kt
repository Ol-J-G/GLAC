package de.oljg.glac.alarms.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedShortDateTimeFormatter
import de.oljg.glac.core.alarms.data.Alarm
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toJavaDuration


enum class AlarmErrorState {
    NO_ERROR,
    DATE_AND_TIME_NOT_SET,
    DATE_NOT_SET,
    TIME_NOT_SET,
    TIME_IS_NOT_IN_FUTURE,
    ALARM_OVERLAPS_EXISTING_ALARM
}

enum class ValidState {
    VALID,
    INVALID
}

fun <T> T?.isSet() = this != null

fun dateAndTimeAreSet(date: LocalDate?, time: LocalTime?) = date.isSet() && time.isSet()


@RequiresApi(Build.VERSION_CODES.O)
fun evaluateAlarmErrorState(
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToUpdate: Alarm? = null
) = when {
    !date.isSet() && !time.isSet() -> AlarmErrorState.DATE_AND_TIME_NOT_SET
    !date.isSet() -> AlarmErrorState.DATE_NOT_SET
    !time.isSet() -> AlarmErrorState.TIME_NOT_SET

    isAlarmStart(ValidState.INVALID, date, time, lightAlarmDuration) ->
        AlarmErrorState.TIME_IS_NOT_IN_FUTURE

    overlapsExistingAlarm(
        ValidState.INVALID,
        date,
        time,
        lightAlarmDuration,
        scheduledAlarms,
        alarmToUpdate
    ) -> AlarmErrorState.ALARM_OVERLAPS_EXISTING_ALARM

    else -> AlarmErrorState.NO_ERROR
}


@RequiresApi(Build.VERSION_CODES.O)
fun checkIfReadyToScheduleAlarm(
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToUpdate: Alarm? = null,
    isLightAlarm: Boolean,
    isValidLightAlarmDuration: Boolean
) = isAlarmStart(
    validState = ValidState.VALID, // >= now
    date = date,
    time = time,
    lightAlarmDuration = lightAlarmDuration
) && overlapsExistingAlarm(
    ValidState.VALID, // => NOT overlaps existing alarm => ready to schedule/update
    date,
    time,
    lightAlarmDuration,
    scheduledAlarms,
    alarmToUpdate // keep this out of overlap calculation in case of update (null otherwise)
) && if (isLightAlarm) isValidLightAlarmDuration else true


// Nice to have as tiny hint for users
@RequiresApi(Build.VERSION_CODES.O)
fun earliestPossibleAlarmTime(lightAlarmDuration: Duration): String =
        localizedShortDateTimeFormatter.format(
            LocalDateTime.now()
                .plus(ALARM_START_BUFFER)
                .plus(lightAlarmDuration)
                .plus(1.minutes) // make current minute exclusive
        )


@RequiresApi(Build.VERSION_CODES.O)
fun isAlarmStart(
    validState: ValidState,
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration
) =
        dateAndTimeAreSet(date, time) // must be non-null to be used in LocalDateTime.of()
                && when (validState) {

            // Valid alarm start => requested start is now or in the future
            ValidState.VALID -> LocalDateTime.of(date, time).isInFuture(lightAlarmDuration)

            // Invalid alarm start => requested start is in the past
            ValidState.INVALID -> !LocalDateTime.of(date, time).isInFuture(lightAlarmDuration)
        }


@RequiresApi(Build.VERSION_CODES.O)
fun overlapsExistingAlarm(
    validState: ValidState,
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToUpdate: Alarm? = null
) =
        dateAndTimeAreSet(date, time) // must be non-null to be used in LocalDateTime.of()
                && when (validState) {
            ValidState.VALID -> !LocalDateTime.of(date, time) // VALID => does NOT interfere
                .interferesScheduledAlarms(lightAlarmDuration, scheduledAlarms, alarmToUpdate)

            ValidState.INVALID -> LocalDateTime.of(date, time) // INVALID => does interfere
                .interferesScheduledAlarms(lightAlarmDuration, scheduledAlarms, alarmToUpdate)
        }


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
    val alarmTime = this.minus(ALARM_START_BUFFER)
    val lightAlarmTime = alarmTime.minus(lightAlarmDuration)
    return LocalDateTime.of(now.year, now.month, now.dayOfMonth, now.hour, now.minute)
        .isBefore(lightAlarmTime)
}


/**
 * ____________________lightAlarmStart-1_________alarmStart-1___lightAlarmStart-N___alarmStart-N____
 *                                    |                    |                   |              |
 *                                    |                    |                   |              |
 *                        valid >|    |                    |    |< valid >|    |              |
 *                               |<4m>|                    |<4m>|         |<4m>|              |<4m>|
 *                               |<        NOT valid           >|         |<      NOT valid       >|
 *         |< lightAlarmDuration>|                              |         |                        |
 *         |                     |                              |         |                        |
 *  lightAlarmStart            this('requestedAlarmStart')      |         |                        |
 *         |                     |                              |         |                        |
 *         |<requestedAlarmRange>|<      invalidRange          >|         |<      invalidRange    >|
 *
 * ALARM_START_BUFFER = 5m
 * @see de.oljg.glac.alarms.ui.utils.OverlappingAlarmsSimpleTest
 * @see de.oljg.glac.alarms.ui.utils.OverlappingAlarmsAdvancedTest
 * @see de.oljg.glac.alarms.ui.utils.OverlappingAlarmsUpdateTest
 */
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.interferesScheduledAlarms(
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToUpdate: Alarm? = null
): Boolean {
    val requestedAlarmRange = this.minus(lightAlarmDuration).rangeUntil(this)

    scheduledAlarms.forEach { scheduledAlarm ->

        // In case an alarm is going to be updated, don't check overlap++ (must be updatable!)
        if (scheduledAlarm.start == alarmToUpdate?.start) return@forEach

        val invalidRange = scheduledAlarm.start
            .minus(if (scheduledAlarm.isLightAlarm) scheduledAlarm.lightAlarmDuration else ZERO)
            .minus(ALARM_START_BUFFER)
            .plus(1.minutes) // to compensate 'virtually exclusive' OpenEndRange's start
            .rangeUntil(
                scheduledAlarm.start
                    .plus(ALARM_START_BUFFER)
                    .minus(1.minutes) // to compensate OpenEndRange's endExclusive
            )
        if (invalidRange.overlapsOrContainsCompletely(requestedAlarmRange))
            return true
    }
    return false
}

/**
 * See [de.oljg.glac.alarms.ui.utils.OverlappingRangesTest], it documents how it works and how
 * to use.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun OpenEndRange<LocalDateTime>.overlapsOrContainsCompletely(
    otherRange: OpenEndRange<LocalDateTime>
): Boolean {
    val start = this.start
    val end = this.endExclusive
    val otherStart = otherRange.start
    val otherEnd = otherRange.endExclusive

    val otherStartIsInThisRange = otherStart in start..end
    val otherEndIsInThisRange = otherEnd in start..end

    val thisContainsOtherRangeCompletely =
            start in otherStart..otherEnd && end in otherStart..otherEnd

    return otherStartIsInThisRange || otherEndIsInThisRange || thisContainsOtherRangeCompletely
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


@RequiresApi(Build.VERSION_CODES.O)
private fun defaultOffset(): ZoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())


@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime?.toEpochMillis() = this?.toEpochSecond(defaultOffset())?.times(1000L)


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
    val DEFAULT_LIGHT_ALARM_DURATION = 30.minutes

    @RequiresApi(Build.VERSION_CODES.O)
    val localizedFullDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

    @RequiresApi(Build.VERSION_CODES.O)
    val localizedShortTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    @RequiresApi(Build.VERSION_CODES.O)
    val localizedShortDateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    val minutesSaver: Saver<Duration, Any> = run {
        val minutes = "minutes"
        mapSaver(
            save = { duration -> mapOf(minutes to duration.toInt(DurationUnit.MINUTES)) },
            restore = { minutesMap -> (minutesMap[minutes] as Int).minutes }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val localDateTimeSaver: Saver<LocalDateTime?, Any> = run {
        val localDateTimeKey = "localDateTime"
        mapSaver(
            save = { localDateTime ->
                mapOf(localDateTimeKey to localDateTime?.format(DateTimeFormatter.ISO_DATE_TIME))
            },
            restore = { localDateTimeMap ->
                localDateTimeMap[localDateTimeKey]?.let {
                    LocalDateTime.parse(it as String, DateTimeFormatter.ISO_DATE_TIME)
                }
            }
        )
    }
}
