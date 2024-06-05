package de.oljg.glac.alarms.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.earliestPossibleAlarmTime
import de.oljg.glac.alarms.ui.utils.evaluateAlarmErrorState
import de.oljg.glac.alarms.ui.utils.isSet
import de.oljg.glac.core.alarms.data.Alarm
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ColumnScope.AlarmDialogInfoSection(
    date: LocalDate?,
    time: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToBeUpdated: Alarm?
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
            scheduledAlarms,
            alarmToBeUpdated
        ),
        isUpdate = alarmToBeUpdated.isSet()
    )
}
