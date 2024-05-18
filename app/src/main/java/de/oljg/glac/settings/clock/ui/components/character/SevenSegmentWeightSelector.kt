package de.oljg.glac.settings.clock.ui.components.character

import androidx.compose.runtime.Composable
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentWeight
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.SEVEN_SEGMENT_WEIGHTS

@Composable
fun SevenSegmentWeightSelector(
    label: String,
    selectedSevenSegmentWeight: SevenSegmentWeight,
    onNewSevenSegmentWeightSelected: (String) -> Unit
) {
    DropDownSelector(
        type = SevenSegmentWeight::class,
        label = label,
        selectedValue = selectedSevenSegmentWeight.name,
        onNewValueSelected = onNewSevenSegmentWeightSelected,
        values = SEVEN_SEGMENT_WEIGHTS,
    )
}
