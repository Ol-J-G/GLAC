package de.oljg.glac.feature_clock.ui.settings.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.feature_clock.ui.clock.utils.SevenSegmentWeight
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.SEVEN_SEGMENT_WEIGHTS

@Composable
fun SevenSegmentWeightSelector(
    label: String,
    selectedSevenSegmentWeight: SevenSegmentWeight,
    onNewSevenSegmentWeightSelected: (String) -> Unit
) {
    DropDownSelector(
        type = SevenSegmentWeight::class,
        startPadding = DIALOG_DEFAULT_PADDING / 3,
        label = label,
        selectedValue = selectedSevenSegmentWeight.name,
        onNewValueSelected = onNewSevenSegmentWeightSelected,
        values = SEVEN_SEGMENT_WEIGHTS,
    )
}
