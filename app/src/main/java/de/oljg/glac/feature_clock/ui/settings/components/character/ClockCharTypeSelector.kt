package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import de.oljg.glac.R
import de.oljg.glac.feature_clock.ui.clock.utils.ClockCharType
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.CLOCK_CHAR_TYPE_FONT_SIZE
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.RADIO_BUTTON_ROW_HEIGHT
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.SETTINGS_HORIZONTAL_PADDING

@Composable
fun ClockCharTypeSelector(
    selectedClockCharType: ClockCharType,
    onClockCharTypeSelected: (ClockCharType) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        Row(Modifier.selectableGroup()) {
            ClockCharType.entries.forEach { clockCharType ->
                Row(
                    Modifier
                        .height(RADIO_BUTTON_ROW_HEIGHT)
                        .selectable(
                            selected = (clockCharType == selectedClockCharType),
                            onClick = { onClockCharTypeSelected(clockCharType) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = SETTINGS_HORIZONTAL_PADDING),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (clockCharType == selectedClockCharType),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        modifier = Modifier.padding(start = SETTINGS_HORIZONTAL_PADDING),
                        text = if (clockCharType == ClockCharType.FONT)
                            stringResource(R.string.font) else stringResource(R.string._7_segment),
                        fontSize = CLOCK_CHAR_TYPE_FONT_SIZE
                    )
                }
            }
        }
    }
}
