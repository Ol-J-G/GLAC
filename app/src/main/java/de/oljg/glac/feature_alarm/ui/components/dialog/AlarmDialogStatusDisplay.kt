package de.oljg.glac.feature_alarm.ui.components.dialog

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.feature_alarm.ui.utils.AlarmErrorState

@Composable
fun AlarmDialogStatusDisplay(
    alarmErrorState: AlarmErrorState,
    isUpdate: Boolean
) {
    Crossfade(targetState = alarmErrorState, label = "ADSDCF") { errorState ->
        when (errorState) {
            AlarmErrorState.TIME_IS_NOT_IN_FUTURE -> DialogMessage(
                isErrorMessage = true,
                message = stringResource(R.string.alarm_time_must_be_in_the_future)
            )

            AlarmErrorState.ALARM_OVERLAPS_EXISTING_ALARM -> DialogMessage(
                isErrorMessage = true,
                message = stringResource(R.string.alarm_time_may_not_overlap_an_existing_alarm)
            )

            AlarmErrorState.DATE_AND_TIME_NOT_SET -> DialogMessage(
                message = stringResource(R.string.please_select_start_date_and_time)
            )

            AlarmErrorState.DATE_NOT_SET -> DialogMessage(
                message = stringResource(R.string.please_select_a_start_date)
            )

            AlarmErrorState.TIME_NOT_SET -> DialogMessage(
                message = stringResource(R.string.please_select_a_start_time)
            )

            AlarmErrorState.INVALID_LIGHT_ALARM_DURATION -> DialogMessage(
                isErrorMessage = true,
                message = stringResource(R.string.invalid_light_alarm_duration)
            )

            AlarmErrorState.INVALID_SNOOZE_DURATION -> DialogMessage(
                isErrorMessage = true,
                message = stringResource(R.string.invalid_snooze_duration)
            )

            AlarmErrorState.NO_ERROR -> DialogMessage(
                message = stringResource(R.string.ready_to) + SPACE + when {
                    isUpdate -> stringResource(R.string.update).lowercase()
                    else -> stringResource(R.string.schedule).lowercase()
                } + SPACE + stringResource(R.string.alarm).lowercase() + "!"
            )
        }
    }
}

