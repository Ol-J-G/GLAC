package de.oljg.glac.feature_alarm.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.ui.utils.earliestPossibleAlarmTime
import de.oljg.glac.feature_alarm.ui.utils.evaluateAlarmErrorState
import de.oljg.glac.feature_alarm.ui.utils.isSet
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration

@Composable
fun ColumnScope.AlarmDialogInfoSection(
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToBeUpdated: Alarm?,
    isValidLightAlarmDuration: Boolean,
    isValidSnoozeDuration: Boolean
) {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outline,
        thickness = SettingsDefaults.DIALOG_BORDER_WIDTH
    )
    DialogMessage( // Hint, always visible
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
            .weight(1f, fill = false),
        message = stringResource(R.string.earliest_possible_alarm_time)
                + earliestPossibleAlarmTime(lightAlarmDuration)
    )
    AlarmDialogStatusDisplay(
        alarmErrorState = evaluateAlarmErrorState(
            date,
            time,
            lightAlarmDuration,
            isValidLightAlarmDuration,
            isValidSnoozeDuration,
            scheduledAlarms,
            alarmToBeUpdated
        ),
        isUpdate = alarmToBeUpdated.isSet()
    )
}
