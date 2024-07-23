package de.oljg.glac.feature_alarm.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.domain.model.Alarm
import de.oljg.glac.feature_alarm.ui.utils.earliestPossibleAlarmTime
import de.oljg.glac.feature_alarm.ui.utils.evaluateAlarmErrorState
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration

@Composable
fun AlarmDialogInfoSection(
    startDate: LocalDate?,
    startTime: LocalTime?,
    lightAlarmDuration: Duration,
    scheduledAlarms: List<Alarm>,
    alarmToBeUpdated: Alarm?,
    isValidLightAlarmDuration: Boolean,
    isValidSnoozeDuration: Boolean
) {
    Column {
        DialogMessage( // Hint, always visible
            message = stringResource(R.string.earliest_possible_alarm_time)
                    + earliestPossibleAlarmTime(lightAlarmDuration)
        )
        AlarmDialogStatusDisplay( // Ready to schedule alarm? => If not => Info why + Hints
            alarmErrorState = evaluateAlarmErrorState(
                startDate,
                startTime,
                lightAlarmDuration,
                isValidLightAlarmDuration,
                isValidSnoozeDuration,
                scheduledAlarms,
                alarmToBeUpdated
            ),
            isUpdate = alarmToBeUpdated != null
        )
    }
}
