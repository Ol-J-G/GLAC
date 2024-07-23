package de.oljg.glac.feature_alarm.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.domain.model.AlarmSettings
import de.oljg.glac.feature_alarm.ui.AlarmSettingsEvent
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


@Composable
fun handleAlarmToBeLaunched(
    alarmSettings: AlarmSettings,
    onEvent: (AlarmSettingsEvent) -> Unit
): Alarm? {
    var alarmToBeLaunched: Alarm? by remember {
        mutableStateOf(null)
    }

    var isHandling by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(isHandling) {
        if (isHandling) { // Handle alarmToBeLaunched exactly one time (per onCreate)

            /**
             * Can't be null => alarms.size must be > 0
             * => Will be called in AlarmActivity where an alarm will be triggerd (all alarm are
             *    stored in alarmSettings.alarms ...
             * => Following call with !! is save
             */
            alarmToBeLaunched = alarmSettings.alarms.minByOrNull { it.start }
            val alarmtoBeUpdated = alarmToBeLaunched!!

            when (alarmtoBeUpdated.repetition) {

                // No repetition => remove, it's not needed anymore (can also be a snooze alarm)
                Repetition.NONE -> {
                    onEvent(AlarmSettingsEvent.RemoveAlarm(alarmtoBeUpdated))
                }

                // Daily => remove current, add and schedule new repetition one day later
                Repetition.DAILY -> {
                    onEvent(
                        AlarmSettingsEvent.UpdateAlarm(
                            alarmtoBeUpdated = alarmtoBeUpdated,
                            updatedAlarm = alarmtoBeUpdated.copy(
                                start = alarmtoBeUpdated.start.plus(1.days)
                            )
                        )
                    )
                }

                // Weekly => remove current, add and schedule new repetition one week(7d) later
                Repetition.WEEKLY -> {
                    onEvent(
                        AlarmSettingsEvent.UpdateAlarm(
                            alarmtoBeUpdated = alarmtoBeUpdated,
                            updatedAlarm = alarmtoBeUpdated.copy(
                                start = alarmtoBeUpdated.start.plus(7.days)
                            )
                        )
                    )
                }

                // Monthly => remove current, add and schedule new repetition one month later
                Repetition.MONTHLY -> {
                    onEvent(
                        AlarmSettingsEvent.UpdateAlarm(
                            alarmtoBeUpdated = alarmtoBeUpdated,
                            updatedAlarm = alarmtoBeUpdated.copy(
                                start = alarmtoBeUpdated.start.plusMonths(1L)
                            )
                        )
                    )
                }
            }
            isHandling = false
        }
    }
    return alarmToBeLaunched
}


@Composable
fun evaluateAlarmRepetitionInfo(
    repetition: Repetition,
    alarmStart: LocalDateTime
) = when (repetition) {

    // E.g.: 'Once at 5/31/24, 5:00 PM '
    Repetition.NONE -> stringResource(R.string.once_at) + SPACE +
            AlarmDefaults.localizedShortDateTimeFormatter.format(alarmStart)

    // E.g.: 'Every day at 5:00 PM'
    Repetition.DAILY ->
        stringResource(R.string.every) + SPACE +
            stringResource(R.string.day) + SPACE +
            stringResource(R.string.at) + SPACE +
            AlarmDefaults.localizedShortTimeFormatter.format(alarmStart)

    // E.g.: 'Every Monday at 5:00 PM'
    Repetition.WEEKLY -> stringResource(R.string.every) + SPACE +
            alarmStart.dayOfWeek.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            ) + SPACE +
            stringResource(R.string.at) + SPACE +
            AlarmDefaults.localizedShortTimeFormatter.format(alarmStart)

    // E.g.: 'Every month on 22th at 5:00 PM '
    Repetition.MONTHLY -> stringResource(R.string.every) + SPACE +
            alarmStart.dayOfMonth.formatAsOrdinal() + SPACE +
            stringResource(R.string.at) + SPACE +
            AlarmDefaults.localizedShortTimeFormatter.format(alarmStart)
}


@Composable
fun Repetition.translate() = when (this) {
    Repetition.NONE -> stringResource(R.string.none)
    Repetition.DAILY -> stringResource(R.string.daily)
    Repetition.WEEKLY -> stringResource(R.string.weekly)
    Repetition.MONTHLY -> stringResource(R.string.monthly)
}


@Composable
fun translateDuration(unit: DurationUnit) = when (unit) {
    DurationUnit.NANOSECONDS -> stringResource(R.string.nanoseconds)
    DurationUnit.MICROSECONDS -> stringResource(R.string.microseconds)
    DurationUnit.MILLISECONDS -> stringResource(R.string.milliseconds)
    DurationUnit.SECONDS -> stringResource(R.string.seconds)
    DurationUnit.MINUTES -> stringResource(R.string.minutes)
    DurationUnit.HOURS -> stringResource(R.string.hours)
    DurationUnit.DAYS -> stringResource(R.string.days)
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
    if (this <= Duration.ZERO) return "0"

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


private fun Duration.toDaysHoursMinutesSeconds() =
        this.toComponents { days, hours, minutes, seconds, _ ->
            buildMap {
                put(DurationUnit.DAYS, days.days.toInt(DurationUnit.DAYS))
                put(DurationUnit.HOURS, hours.hours.toInt(DurationUnit.HOURS))
                put(DurationUnit.MINUTES, minutes.minutes.toInt(DurationUnit.MINUTES))
                put(DurationUnit.SECONDS, seconds.seconds.toInt(DurationUnit.SECONDS))
            }
        }
