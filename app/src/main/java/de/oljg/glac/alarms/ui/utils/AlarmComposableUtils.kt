package de.oljg.glac.alarms.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.oljg.glac.R
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.settings.alarms.ui.AlarmSettingsViewModel
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit


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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun evaluateAlarmRepetitionInfo(
    repetition: Repetition,
    alarmStart: LocalDateTime
) = when (repetition) {

    // E.g.: 'Once at 5/31/24, 5:00 PM '
    Repetition.NONE -> stringResource(R.string.once) + AlarmDefaults.SPACE +
            stringResource(R.string.at) + AlarmDefaults.SPACE +
            AlarmDefaults.localizedShortDateTimeFormatter.format(alarmStart)

    // E.g.: 'Every day at 5:00 PM'
    Repetition.DAILY -> stringResource(R.string.every) + AlarmDefaults.SPACE +
            stringResource(R.string.day) + AlarmDefaults.SPACE +
            stringResource(R.string.at) + AlarmDefaults.SPACE +
            AlarmDefaults.localizedShortTimeFormatter.format(alarmStart)

    // E.g.: 'Every Monday at 5:00 PM'
    Repetition.WEEKLY -> stringResource(R.string.every) + AlarmDefaults.SPACE +
            alarmStart.dayOfWeek.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            ) + AlarmDefaults.SPACE +
            stringResource(R.string.at) + AlarmDefaults.SPACE +
            AlarmDefaults.localizedShortTimeFormatter.format(alarmStart)

    // E.g.: 'Every month on 22th at 5:00 PM '
    Repetition.MONTHLY -> stringResource(R.string.every) + AlarmDefaults.SPACE +
            stringResource(R.string.month) + AlarmDefaults.SPACE +
            stringResource(R.string.on_) + AlarmDefaults.SPACE +
            alarmStart.dayOfMonth.formatAsOrdinal() + AlarmDefaults.SPACE +
            stringResource(R.string.at) + AlarmDefaults.SPACE +
            AlarmDefaults.localizedShortTimeFormatter.format(alarmStart)
}


@Composable
fun Repetition.translate() = when (this) {
    Repetition.NONE -> stringResource(R.string.none)
    Repetition.DAILY -> stringResource(R.string.daily)
    Repetition.WEEKLY -> stringResource(R.string.weekly)
    Repetition.MONTHLY -> stringResource(R.string.monthly)
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