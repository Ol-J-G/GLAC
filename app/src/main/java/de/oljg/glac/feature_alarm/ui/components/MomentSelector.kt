package de.oljg.glac.feature_alarm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.MOMENT_SELECTOR_PADDING
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.localizedFullDateFormatter
import de.oljg.glac.feature_alarm.ui.utils.AlarmDefaults.localizedShortTimeFormatter
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MomentSelector(
    label: String,
    labelStartPadding: Dp = 0.dp,
    dateMoment: LocalDate? = null,
    timeMoment: LocalTime? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = MOMENT_SELECTOR_PADDING)
            .padding(top = MOMENT_SELECTOR_PADDING)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modifier = Modifier.padding(start = labelStartPadding), text = label)
        TextButton(onClick = onClick) {
            Text(
                text = when {
                    dateMoment != null -> localizedFullDateFormatter.format(dateMoment)
                    timeMoment != null -> localizedShortTimeFormatter.format(timeMoment)
                    else -> stringResource(R.string.select).uppercase()
                }
            )
        }
    }
}
