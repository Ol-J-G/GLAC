package de.oljg.glac.feature_alarm.ui.utils

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.unit.dp
import com.ibm.icu.text.RuleBasedNumberFormat
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.localizedShortDateTimeFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.days
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
    ALARM_OVERLAPS_EXISTING_ALARM,
    INVALID_LIGHT_ALARM_DURATION,
    INVALID_SNOOZE_DURATION
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


fun evaluateAlarmErrorState(
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration,
    isValidLightAlarmDuration: Boolean,
    isValidSnoozeDuration: Boolean,
    scheduledAlarms: List<Alarm>,
    alarmToUpdate: Alarm? = null
) = when {
    !date.isSet() && !time.isSet() -> AlarmErrorState.DATE_AND_TIME_NOT_SET
    !date.isSet() -> AlarmErrorState.DATE_NOT_SET
    !time.isSet() -> AlarmErrorState.TIME_NOT_SET

    !isValidLightAlarmDuration -> AlarmErrorState.INVALID_LIGHT_ALARM_DURATION
    !isValidSnoozeDuration -> AlarmErrorState.INVALID_SNOOZE_DURATION

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


fun checkIfReadyToScheduleAlarm(
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToUpdate: Alarm? = null,
    isLightAlarm: Boolean,
    isValidLightAlarmDuration: Boolean,
    isValidSnoozeDuration: Boolean
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
) && isValidSnoozeDuration
        && if (isLightAlarm) isValidLightAlarmDuration else true


// Nice to have as tiny hint for users
fun earliestPossibleAlarmTime(lightAlarmDuration: Duration): String =
        localizedShortDateTimeFormatter.format(
            LocalDateTime.now()
                .plus(ALARM_START_BUFFER)
                .plus(lightAlarmDuration)
                .plus(1.minutes) // make current minute exclusive
        )


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
 * @see de.oljg.glac.feature_alarm.ui.utils.OverlappingAlarmsSimpleTest
 * @see de.oljg.glac.feature_alarm.ui.utils.OverlappingAlarmsAdvancedTest
 * @see de.oljg.glac.feature_alarm.ui.utils.OverlappingAlarmsUpdateTest
 */
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

        when (scheduledAlarm.repetition) {
            Repetition.NONE -> {
                if (rangesOverlap(requestedAlarmRange, scheduledAlarm))
                    return true
            }

            Repetition.DAILY -> { // look 1 year in the future
                repeat(367) { day -> // zero-based => +1, inclusive => +1 => 365 + 2
                    val dailyRepeatingAlarm = scheduledAlarm.copy(
                        start = scheduledAlarm.start.plus(day * 1.days)
                    )
                    if (rangesOverlap(requestedAlarmRange, dailyRepeatingAlarm))
                        return true
                }
            }

            Repetition.WEEKLY -> { // look 1 year in the future
                repeat(54) { week -> // zero-based => +1, inclusive => +1 => 52 + 2
                    val weeklyRepeatingAlarm = scheduledAlarm.copy(
                        start = scheduledAlarm.start.plus(week * 7.days)
                    )
                    if (rangesOverlap(requestedAlarmRange, weeklyRepeatingAlarm))
                        return true
                }
            }

            Repetition.MONTHLY -> { // look 1 year in the future
                repeat(13) { month -> // zero-based => +1 => 12 + 1
                    val mothlyRepeatingAlarm = scheduledAlarm.copy(
                        start = scheduledAlarm.start.plusMonths(month * 1L)
                    )
                    if (rangesOverlap(requestedAlarmRange, mothlyRepeatingAlarm))
                        return true
                }
            }
        }
    }
    return false
}


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
 * See [de.oljg.glac.feature_alarm.ui.utils.OverlappingRangesTest], it documents how it works and how
 * to use.
 */
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
 * Let user max. ALARM_START_BUFFER.minutes to react on snooze alarm, before the next scheduled
 * alarm is going to be launched.
 *
 *        |<     ALARM_START_BUFFER     >|
 *        |                              |-----Next-Alarm-----|
 *        |                              |
 *  snoozeAlarmStart                 nextAlarmStart
 */
fun isSnoozeAlarmBeforeNextAlarm(
    snoozeAlarmStart: LocalDateTime,
    scheduledAlarms: List<Alarm>
): Boolean {
    val nextAlarmStart = scheduledAlarms.minByOrNull { it.start }?.start ?: return true
    return snoozeAlarmStart.isBefore((nextAlarmStart).minus(ALARM_START_BUFFER))
}


/**
 * Use [kotlin.time.Duration] together with [java.time.LocalDateTime] to
 * substract a duration from a local date time object.
 * (better for now to use [kotlin.time.Duration.toJavaDuration] just once, here in this fun ...
 * (sadly, kotlin time lib is still experimental (today=2024-05-21), so relying on Java here :>))
 */
operator fun LocalDateTime.minus(amountToSubstract: Duration): LocalDateTime =
        minus(amountToSubstract.toJavaDuration())


operator fun LocalDateTime.plus(amountToAdd: Duration): LocalDateTime =
        plus(amountToAdd.toJavaDuration())


operator fun LocalTime.plus(amountToAdd: Duration): LocalTime =
        plus(amountToAdd.toJavaDuration())


fun Duration.Companion.between(a: LocalDateTime, b: LocalDateTime): Duration {
    if (a == b || b.isBefore(a)) return ZERO // handles positive durations only!
    return java.time.Duration.between(a, b).toKotlinDuration()
}


private fun defaultOffset(): ZoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())

fun LocalDateTime?.toEpochMillis() = this?.toEpochSecond(defaultOffset())?.times(1000L)

fun LocalDateTime?.toEpochMillisUTC() = this?.toEpochSecond(ZoneOffset.UTC)?.times(1000L)

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


fun Alarm?.isLightAlarm() = this != null && this.isLightAlarm

fun Alarm?.isNotLightAlarm() = this != null && !this.isLightAlarm


fun List<Alarm>.filterAndSort() = this
    .filter { alarm -> !alarm.isSnoozeAlarm } // Keep snooze alarms under the hood
    .sortedBy { alarm -> alarm.start } // ASC => next alarm is always on top start/left side,


/**
 * Rearranges a sorted(ASC) list of alarms by rows as follows:
 * |0|1|
 * |2|3|  => Yes, unusual! But imho more natural/intuitive in this alarm list scenario
 * |4|5|
 * To be used in a vertically scrollable row with two columns
 * => When looking chronologically through alarms => No need to scroll that much ...!
 *
 * Alternatively, arranged by columns would be another option (similar to clock settings screen)
 * E.g.: Split alarms with List.chunked( size = ceil(alarms.size / 2f).toInt() )
 * |0|3|
 * |1|4|  => No way! ... . (manual tests have shown that this is annoying to use ... ¯\_(ツ)_/¯ ...)
 * |2|5|
 */
fun List<Alarm>.rearrange(): Pair<List<Alarm>, List<Alarm>> {
    val evenIndices = emptyList<Alarm>().toMutableList()
    val oddIndices = emptyList<Alarm>().toMutableList()
    this.forEachIndexed { index, alarm ->
        when {
            index % 2 == 0 -> evenIndices.add(alarm)
            else -> oddIndices.add(alarm)
        }
    }
    return Pair(evenIndices.toList(), oddIndices.toList())
}

@OptIn(ExperimentalMaterial3Api::class)
object PresentSelectableDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        // Disable days in the past, excluding today
        return Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate()
            .isAfter(LocalDate.now().minusDays(1L))
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year + 1
    }
}


object AlarmDefaults {
    const val PREVIEW_PLAYER_CORNER_SIZE_PERCENT = 50
    val PREVIEW_PLAYER_ELEVATION = 8.dp
    val PREVIEW_PLAYER_PADDING = 8.dp
    val LIST_ITEM_TEXT_ICON_SPACE = 8.dp
    const val ALARM_REACTION_DIALOG_BUTTON_WEIGHT = 1.7f
    const val ALARM_REACTION_DIALOG_DISMISS_BUTTON_WEIGHT = 1f

    val MIN_LIGHT_ALARM_DURATION = 1.minutes
    val MAX_LIGHT_ALARM_DURATION = 60.minutes

    val MIN_SNOOZE_DURATION = 5.minutes
    val MAX_SNOOZE_DURATION = 60.minutes

    val MIN_ALARM_SOUND_FADE_DUARTION = ZERO
    val MAX_ALARM_SOUND_FADE_DUARTION = 60.seconds

    // Users can schedule an alarm from now + ALARM_START_BUFFER
    val ALARM_START_BUFFER = 2.minutes

    val REPEAT_MODES = Repetition.entries.map { repeatMode -> repeatMode.name }

    val localizedFullDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

    val localizedShortTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    val localizedShortDateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    val minutesSaver: Saver<Duration, Any> = run {
        val minutes = "minutes"
        mapSaver(
            save = { duration -> mapOf(minutes to duration.toInt(DurationUnit.MINUTES)) },
            restore = { minutesMap -> (minutesMap[minutes] as Int).minutes }
        )
    }

    val uriSaver: Saver<Uri, Any> = run {
        val uriKey = "uri"
        mapSaver(
            save = { uri -> mapOf(uriKey to uri.toString()) },
            restore = { uriMap -> Uri.parse(uriMap[uriKey].toString()) }
        )
    }

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
