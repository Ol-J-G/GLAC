package de.oljg.glac.settings.clock.ui.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentStyle
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SEVEN_SEGMENT_STYLES

@Composable
fun SevenSegmentStyleSelector(
    label: String,
    selectedSevenSegmentStyle: SevenSegmentStyle,
    onNewSevenSegmentStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        type = SevenSegmentStyle::class,
        startPadding = DIALOG_DEFAULT_PADDING / 3,
        label = label,
        selectedValue = selectedSevenSegmentStyle.name,
        onNewValueSelected = onNewSevenSegmentStyleSelected,
        values = SEVEN_SEGMENT_STYLES,
    )
}

