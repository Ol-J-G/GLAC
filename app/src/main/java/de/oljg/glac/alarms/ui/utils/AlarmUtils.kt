package de.oljg.glac.alarms.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.res.stringResource
import com.ibm.icu.text.RuleBasedNumberFormat
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.SPACE
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedShortDateTimeFormatter
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedShortTimeFormatter
import de.oljg.glac.core.alarms.data.Alarm
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration


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

enum class Repetition {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY
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
        /**
         * In case an alarm is going to be updated, don't check itself for overlap++
         * (must be updatable!), but, alarm to update may not interfere with any other scheduled
         * alarms.
         */
        if (scheduledAlarm.start == alarmToUpdate?.start) return@forEach

        when(scheduledAlarm.repetition) {
            Repetition.NONE -> {
                if (rangesOverlap(requestedAlarmRange, scheduledAlarm))
                    return true
            }
            Repetition.DAILY -> { // look 1 year in the future
                repeat(367) { day -> // zero-based => +1, inclusive => +1 => 365 + 2
                    val dailyRepeatingAlarm = scheduledAlarm.copy(
                        start = scheduledAlarm.start.plus(day * 1.days))
                    if (rangesOverlap(requestedAlarmRange, dailyRepeatingAlarm))
                        return true
                }
            }
            Repetition.WEEKLY -> { // look 1 year in the future
                repeat(54) { week -> // zero-based => +1, inclusive => +1 => 52 + 2
                    val weeklyRepeatingAlarm = scheduledAlarm.copy(
                        start = scheduledAlarm.start.plus(week * 7.days))
                    if (rangesOverlap(requestedAlarmRange, weeklyRepeatingAlarm))
                        return true
                }
            }
            Repetition.MONTHLY -> { // look 1 year in the future
                repeat(13) { month -> // zero-based => +1 => 12 + 1
                    val mothlyRepeatingAlarm = scheduledAlarm.copy(
                        start = scheduledAlarm.start.plusMonths(month * 1L))
                    if (rangesOverlap(requestedAlarmRange, mothlyRepeatingAlarm))
                        return true
                }
            }
        }
    }
    return false
}

@RequiresApi(Build.VERSION_CODES.O)
private fun rangesOverlap(
    requestedAlarmRange: OpenEndRange<LocalDateTime>,
    scheduledAlarm: Alarm
): Boolean {
    val invalidRange = scheduledAlarm.start
        .minus(if (scheduledAlarm.isLightAlarm) scheduledAlarm.lightAlarmDuration else ZERO)
        .minus(ALARM_START_BUFFER)
        .plus(1.minutes) // to compensate 'virtually exclusive' OpenEndRange's start
        .rangeUntil(
            scheduledAlarm.start
                .plus(ALARM_START_BUFFER)
                .minus(1.minutes) // to compensate OpenEndRange's endExclusive
        )
    return (invalidRange.overlapsOrContainsCompletely(requestedAlarmRange))
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
fun Duration.Companion.between(a: LocalDateTime, b: LocalDateTime): Duration {
    if(a == b || b.isBefore(a)) return ZERO // handles positive durations only!
    return java.time.Duration.between(a, b).toKotlinDuration()
}


private fun Duration.toDaysHoursMinutesSeconds() =
        this.toComponents { days, hours, minutes, seconds, _ ->
            buildMap {
                put(DurationUnit.DAYS, days.days.toInt(DurationUnit.DAYS))
                put(DurationUnit.HOURS, hours.hours.toInt(DurationUnit.HOURS))
                put(DurationUnit.MINUTES, minutes.minutes.toInt(DurationUnit.MINUTES))
                put(DurationUnit.SECONDS, seconds.seconds.toInt(DurationUnit.SECONDS))
            }
        }


/**
 * Mainly to omit the 'flipping' (string changes length when value is between 0..9).
 * To prevent this, add some padding before H, M and S.
 * Another benefit is i18n support.
 *
 * Note that this does not handle negative [Duration]s!
 * Because it's used to format a countdown timer in alarm context, where alarms will be
 * removed after being triggered, so, no need to see negative duration.
 */
@Composable
fun Duration.format(separator: Char? = ' '): String {
    if (this <= ZERO) return "0"

    val components = this.toDaysHoursMinutesSeconds()
    val daysComponent = components.getValue(DurationUnit.DAYS)
    val hoursComponent = components.getValue(DurationUnit.HOURS)
    val minutesComponent = components.getValue(DurationUnit.MINUTES)
    val secondsComponent = components.getValue(DurationUnit.SECONDS)

    return buildString {
        if (daysComponent > 0) {
            append(daysComponent)
            append(stringResource(id = R.string.days_shortened).lowercase())
            separator?.let { append(it) }
        }
        if (hoursComponent > 0) {
            append(hoursComponent.pad())
            append(stringResource(id = R.string.hours_shortened).lowercase())
            separator?.let { append(it) }
        }
        if (minutesComponent > 0) {
            append(minutesComponent.pad())
            append(stringResource(id = R.string.minutes_shortened).lowercase())
            separator?.let { append(it) }
        }
        append(secondsComponent.pad())
        append(stringResource(id = R.string.seconds_shortened).lowercase())
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun defaultOffset(): ZoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())


@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime?.toEpochMillis() = this?.toEpochSecond(defaultOffset())?.times(1000L)

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toMillis() = this.toEpochMillis()!!


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

fun Int.pad(paddingRepetitions: Int = 1, paddingCharacter: Char = SPACE) = when (this) {
    in 0..9 -> buildString {
        repeat(paddingRepetitions) {
            append(paddingCharacter)
        }
    } + this

    else -> this.toString()
}


/**
 * Used to format day of month as ordinal for alarms with [Repetition.MONTHLY], as part of an
 * alarm list item within alarms list screen.
 *
 * Example: 1 => 1st, 10 => 10th (or with most non-US Locale: 1 => 1., 10 => 10.) etc.
 */
fun Int.formatAsOrdinal(): String {
    val ordinalFormatter = RuleBasedNumberFormat(Locale.getDefault(), RuleBasedNumberFormat.ORDINAL)
    return ordinalFormatter.format(this)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun evaluateAlarmRepetitionInfo(
    repetition: Repetition,
    alarmStart: LocalDateTime
) = when (repetition) {

    // E.g.: 'Once at 5/31/24, 5:00 PM '
    Repetition.NONE -> stringResource(R.string.once) + SPACE +
            stringResource(R.string.at) + SPACE +
            localizedShortDateTimeFormatter.format(alarmStart)

    // E.g.: 'Every day at 5:00 PM'
    Repetition.DAILY -> stringResource(R.string.every) + SPACE +
            stringResource(R.string.day) + SPACE +
            stringResource(R.string.at) + SPACE +
            localizedShortTimeFormatter.format(alarmStart)

    // E.g.: 'Every Monday at 5:00 PM'
    Repetition.WEEKLY -> stringResource(R.string.every) + SPACE +
            alarmStart.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()) + SPACE +
            stringResource(R.string.at) + SPACE +
            localizedShortTimeFormatter.format(alarmStart)

    // E.g.: 'Every month on 22th at 5:00 PM '
    Repetition.MONTHLY -> stringResource(R.string.every) + SPACE +
            stringResource(R.string.month) + SPACE +
            stringResource(R.string.on_) + SPACE +
            alarmStart.dayOfMonth.formatAsOrdinal() + SPACE +
            stringResource(R.string.at) + SPACE +
            localizedShortTimeFormatter.format(alarmStart)
}


@Composable
fun Repetition.translate() = when (this) {
    Repetition.NONE -> stringResource(R.string.none)
    Repetition.DAILY -> stringResource(R.string.daily)
    Repetition.WEEKLY -> stringResource(R.string.weekly)
    Repetition.MONTHLY -> stringResource(R.string.monthly)
}


object AlarmDefaults {
    const val SPACE = ' '

    val MIN_LIGHT_ALARM_DURATION = 1.minutes
    val MAX_LIGHT_ALARM_DURATION = 60.minutes

    // Users can schedule an alarm from now + ALARM_START_BUFFER
    val ALARM_START_BUFFER = 1.minutes //TODO: change back to 5 after testing
    val DEFAULT_LIGHT_ALARM_DURATION = 30.minutes

    val REPEAT_MODES = Repetition.entries.map { repeatMode -> repeatMode.name }

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
