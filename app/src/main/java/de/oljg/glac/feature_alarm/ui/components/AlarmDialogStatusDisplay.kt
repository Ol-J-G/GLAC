package de.oljg.glac.feature_alarm.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.ui.utils.AlarmErrorState
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults

@Composable
fun ColumnScope.AlarmDialogStatusDisplay(
    alarmErrorState: AlarmErrorState,
    isUpdate: Boolean
) {
    Crossfade(targetState = alarmErrorState, label = "ADSDCF") { errorState ->
        when (errorState) {
            AlarmErrorState.TIME_IS_NOT_IN_FUTURE -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                isErrorMessage = true,
                message = stringResource(R.string.alarm_time_must_be_in_the_future)
            )

            AlarmErrorState.ALARM_OVERLAPS_EXISTING_ALARM -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                isErrorMessage = true,
                message = stringResource(R.string.alarm_time_may_not_overlap_an_existing_alarm)
            )

            AlarmErrorState.DATE_AND_TIME_NOT_SET -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                message = stringResource(R.string.please_select_start_date_and_time)
            )

            AlarmErrorState.DATE_NOT_SET -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                message = stringResource(R.string.please_select_a_start_date)
            )

            AlarmErrorState.TIME_NOT_SET -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                message = stringResource(R.string.please_select_a_start_time)
            )

            AlarmErrorState.INVALID_LIGHT_ALARM_DURATION -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                isErrorMessage = true,
                message = stringResource(R.string.invalid_light_alarm_duration)
            )

            AlarmErrorState.INVALID_SNOOZE_DURATION -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                isErrorMessage = true,
                message = stringResource(R.string.invalid_snooze_duration)
            )

            AlarmErrorState.NO_ERROR -> DialogMessage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsDefaults.DEFAULT_VERTICAL_SPACE / 2)
                    .weight(1f, fill = false),
                message = stringResource(R.string.ready_to) + " " + when {
                    isUpdate -> stringResource(R.string.update).lowercase()
                    else -> stringResource(R.string.schedule).lowercase()
                } + " " + stringResource(R.string.alarm).lowercase() + "!"
            )
        }
    }
}

