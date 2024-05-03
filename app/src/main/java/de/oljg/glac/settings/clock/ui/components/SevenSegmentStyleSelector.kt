package de.oljg.glac.settings.clock.ui.components

import androidx.compose.runtime.Composable
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SEVEN_SEGMENT_STYLES
import de.oljg.glac.settings.clock.ui.utils.prettyPrintEnumName

@Composable
fun SevenSegmentStyleSelector(
    label: String,
    selectedSevenSegmentStyle: String,
    onNewSevenSegmentStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        label = label,
        selectedValue = selectedSevenSegmentStyle,
        onNewValueSelected = onNewSevenSegmentStyleSelected,
        values = SEVEN_SEGMENT_STYLES,
        prettyPrintValue = String::prettyPrintEnumName,
        maxWidthFraction = .8f
    )
}

