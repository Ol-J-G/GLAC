package de.oljg.glac.alarms.ui.components

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
import de.oljg.glac.R
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedFullDateFormatter
import de.oljg.glac.alarms.ui.utils.AlarmDefaults.localizedShortTimeFormatter
import de.oljg.glac.alarms.ui.utils.isSet
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MomentSelector(
    label: String,
    dateMoment: LocalDate? = null,
    timeMoment: LocalTime? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = DIALOG_DEFAULT_PADDING)
            .padding(top = DEFAULT_VERTICAL_SPACE)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        TextButton(onClick = onClick) {
            Text(
                text = when {
                    dateMoment.isSet() -> localizedFullDateFormatter.format(dateMoment)
                    timeMoment.isSet() -> localizedShortTimeFormatter.format(timeMoment)
                    else -> stringResource(R.string.select).uppercase()
                }
            )
        }
    }
}
