package de.oljg.glac.alarms.ui.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ibm.icu.text.RuleBasedNumberFormat
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.ALARM_START_BUFFER
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.SPACE
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedShortDateTimeFormatter
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedShortTimeFormatter
import de.oljg.glac.clock.digital.ui.utils.findActivity
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
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


@RequiresApi(Build.VERSION_CODES.O)
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


@RequiresApi(Build.VERSION_CODES.O)
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
 * Let user max. ALARM_START_BUFFER.minutes to react on snooze alarm, before the next scheduled
 * alarm is going to be launched.
 *
 *        |<     ALARM_START_BUFFER     >|
 *        |                              |-----Next-Alarm-----|
 *        |                              |
 *  snoozeAlarmStart                 nextAlarmStart
 */
@RequiresApi(Build.VERSION_CODES.O)
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
    if (a == b || b.isBefore(a)) return ZERO // handles positive durations only!
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun handleAlarmToBeLaunched(viewModel: AlarmSettingsViewModel = hiltViewModel()): Alarm? {
    val alarmSettings by viewModel.alarmSettingsStateFlow.collectAsState()
    var alarmToBeLaunched: Alarm? by remember {
        mutableStateOf(null)
    }
    LaunchedEffect(alarmToBeLaunched == null) {
        // Can't be null => alarms.size must be > 0 => following calls with !! are save
        alarmToBeLaunched = alarmSettings.alarms.minByOrNull { it.start }
        val alarmtoBeUpdated = alarmToBeLaunched!!

        when (alarmtoBeUpdated.repetition) {

            // No repetition => remove, it's not needed anymore (can also be a snooze alarm)
            Repetition.NONE -> {
                viewModel.removeAlarm(alarmSettings, alarmtoBeUpdated)
            }

            // Daily => remove current, add and schedule new repetition one day later
            Repetition.DAILY -> {
                viewModel.updateAlarm(
                    alarmSettings,
                    alarmtoBeUpdated,
                    updatedAlarm = alarmtoBeUpdated.copy(
                        start = alarmtoBeUpdated.start.plus(1.days)
                    )
                )
            }

            // Weekly => remove current, add and schedule new repetition one week(7d) later
            Repetition.WEEKLY -> {
                viewModel.updateAlarm(
                    alarmSettings,
                    alarmtoBeUpdated,
                    updatedAlarm = alarmtoBeUpdated.copy(
                        start = alarmtoBeUpdated.start.plus(7.days)
                    )
                )
            }

            // Monthly => remove current, add and schedule new repetition one month later
            Repetition.MONTHLY -> {
                viewModel.updateAlarm(
                    alarmSettings,
                    alarmtoBeUpdated,
                    updatedAlarm = alarmtoBeUpdated.copy(
                        start = alarmtoBeUpdated.start.plusMonths(1L)
                    )
                )
            }
        }
    }
    return alarmToBeLaunched
}

@Composable
fun LightAlarm(
    alarmToBeLaunched: Alarm,
    lightAlarmColors: List<Color>,
    lightAlarmAnimatedColor: Animatable<Color, AnimationVector4D>,
    clockBrightness: Float?,
) {
    /**
     * Lock current screen orientation (user cannot rotate) until light alarm animation has ended.
     * Otherwise, when a user would rotate device during light alarm animation, the animation
     * would be restarted, which would bring the alarm process timeline in an inconsistent state!
     *
     * Of course, nevertheless this is unfortunately a 'dirty' workaround.
     *
     * But, assuming this is a rare case, it's not worth to take care about pause/continue animation
     * on screen rotation, because it's way too complicated imho (write a Savable for
     * Animatable<Color, AnimationVector4D> to use rememberSavable? => uh-oh..uhm, dunno
     * how to do this yet...maybe trying it sometimes).
     *
     * And, usually, a user snoozes or stops the alarm, rather than grap and rotate the device
     * beforehand (at least when user is about to wake up and still tired => don't move too
     * much then, right? :>).
     */
    val currentOrientation = LocalConfiguration.current.orientation
    val activity = LocalContext.current.findActivity()
    activity?.let {
        it.requestedOrientation = when (currentOrientation) {
            Configuration.ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    val lightAlarmDurationMillis = alarmToBeLaunched.lightAlarmDuration.inWholeMilliseconds
    FadeBrightnessFromCurrentToFull(
        clockBrightness = clockBrightness,
        totalDurationMillis = lightAlarmDurationMillis.toInt()
    )

    LaunchedEffect(Unit) {
        /**
         * Example (with default (sunrise) settings)
         *
         * lightAlarmColors.size   = 6
         * lightAlarmDuration      = 30m
         * colorTransitionDuration = 30m / (6-1) = 6m (in millis) from color to color
         *
         *               lightAlarmStart                              actual alarmStart(sound)
         *                     |<                         30m                        >|
         *                     |                                                      |
         *                initialValue
         * lightAlarmA.C. => Black       Blue    LightBlue    Orange   Goldenrod    White
         *                     |          |          |          |          |          |
         *                     |<   6m   >|<   6m   >|<   6m   >|<   6m   >|<   6m   >|
         */
        val colorTransitionDuration =
                (lightAlarmDurationMillis / (lightAlarmColors.size - 1)).toInt()

        // drop(1) => first() already consumed as initial color
        lightAlarmColors.drop(1).forEach { nextColor ->
            lightAlarmAnimatedColor.animateTo(
                nextColor,
                animationSpec = tween(
                    durationMillis = colorTransitionDuration,
                    easing = LinearEasing
                )
            )
        }

        // Light alarm is finished => Unlock screen orientation => let user rotate again
        activity?.let {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // -1
        }
        // TODO: play alarm sound => actual alarm time is reached exactly here
    }
}


/**
 * activity.window.attributes.screenBrightness returns by default
 * WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE (-1) => what user set up.
 *
 * To get the device's actual screen brightness a user has been set, it seems to be necessary to
 * use Settings.System.SCREEN_BRIGHTNESS.
 *
 * To use this value (int, 0..255 / dark..bright) as input for [setScreenBrightness] it needs
 * to be converted to Float (0f..1f / dark..bright).
 */
fun getScreenBrightness(context: Context) = Settings.System.getInt(
    context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, -1
) / 255f


fun setScreenBrightness(activity: Activity, brightness: Float) {
    activity.window.attributes = activity.window.attributes.apply {
        screenBrightness = brightness
    }
}


fun resetScreenBrightness(activity: Activity) {
    setScreenBrightness(activity, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) // -1f
}


@Composable
fun FadeBrightnessFromCurrentToFull(
    totalDurationMillis: Int,
    clockBrightness: Float? = null,
    maxSteps: Int = 100
) {
    require(maxSteps in 1..100)
    val context = LocalContext.current
    val activity = context.findActivity() ?: return

    // Fade animation initial value, by default device's current screen brightness
    val initialBrightness = clockBrightness ?: getScreenBrightness(context)

    /**
     * In case screen is (almost) on full brightness => no need to fade
     * Furthermore, step calculation below would lead to a division by zero!
     * E.g.:
     * * OK  => 1f - .99f  = .01f  * 100 = 1f  toInt() => 1
     * * dbz => 1f - .991f = .009f * 100 = .9f toInt() => 0 !!! => BÄM :>
     */
    if (initialBrightness > .99f) return

    /**
     * 1f => Full brightness is the goal for default sunrise light alarm => "simulates" bright sun.
     *
     * But, whatever color the user chooses as the last color of the light alarm, the room in which
     * the alarm went off will definitely be a little brighter than without fading brightness at
     * all, except for very dark tones ofc (and, .. OLEDs!), ... hmm, don't really like
     * this flaw :/.
     * TODO_LATER: Think about introducing a setting to disable fading brightness during light alarm
     */
    val targetBrightness = 1f

    // 1..[maxSteps] steps (between brightness of 0f..0.99f / fulldark..almost_fullbright)
    val steps = ((targetBrightness - initialBrightness) * maxSteps).toInt()
    val step = targetBrightness / maxSteps
    var nextBrightness = initialBrightness + step
    val durationPerStep = totalDurationMillis / steps

    val brightness = remember { Animatable(initialBrightness) }
    LaunchedEffect(Unit) {
        repeat(steps) {
            brightness.animateTo(
                targetValue = nextBrightness,
                animationSpec = tween(durationMillis = durationPerStep, easing = LinearEasing)
            )
            setScreenBrightness(activity, brightness.value)
            nextBrightness += step
        }
    }
}


fun Alarm?.isSetAndLightAlarm() = this != null && this.isLightAlarm

fun Alarm?.isSetAndSnoozeAlarm() = this != null && this.isSnoozeAlarm

fun Alarm?.isSetAndSoundAlarm() = this != null && !this.isLightAlarm && !this.isSnoozeAlarm


@Composable
fun Repetition.translate() = when (this) {
    Repetition.NONE -> stringResource(R.string.none)
    Repetition.DAILY -> stringResource(R.string.daily)
    Repetition.WEEKLY -> stringResource(R.string.weekly)
    Repetition.MONTHLY -> stringResource(R.string.monthly)
}


object AlarmDefaults {
    const val SPACE = ' '
    const val ALARM_REACTION_DIALOG_BUTTON_WEIGHT = 1.7f
    const val ALARM_REACTION_DIALOG_DISMISS_BUTTON_WEIGHT = 1f

    val MIN_LIGHT_ALARM_DURATION = 1.minutes
    val MAX_LIGHT_ALARM_DURATION = 60.minutes

    val MIN_SNOOZE_DURATION = 5.minutes
    val MAX_SNOOZE_DURATION = 60.minutes

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
// TODO: <=== look at this line nr: 803, => split up this monster file (How could it have come to this .OO.)