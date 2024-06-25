package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentStyle
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.SEVEN_SEGMENT_STYLES

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

