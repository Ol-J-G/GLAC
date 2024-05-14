package de.oljg.glac.settings.clock.ui.components.character

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
import de.oljg.glac.clock.digital.ui.utils.ClockCharType
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.CLOCK_CHAR_TYPES
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.CLOCK_CHAR_TYPE_FONT_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.RADIO_BUTTON_ROW_HEIGHT
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SETTINGS_HORIZONTAL_PADDING

@Composable
fun ClockCharTypeSelector(
    label: String,
    selectedClockCharType: String,
    onClockCharTypeSelected: (String) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(label)
        Row(Modifier.selectableGroup()) {
            CLOCK_CHAR_TYPES.forEach { text ->
                Row(
                    Modifier
                        .height(RADIO_BUTTON_ROW_HEIGHT)
                        .selectable(
                            selected = (text == selectedClockCharType),
                            onClick = { onClockCharTypeSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = SETTINGS_HORIZONTAL_PADDING),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedClockCharType),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        modifier = Modifier.padding(start = SETTINGS_HORIZONTAL_PADDING),
                        text = if (text == ClockCharType.FONT.name) stringResource(R.string.font)
                        else stringResource(R.string._7_segment),
                        fontSize = CLOCK_CHAR_TYPE_FONT_SIZE
                    )
                }
            }
        }
    }
}
