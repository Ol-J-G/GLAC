package de.oljg.glac.feature_clock.ui.settings.components.divider

import androidx.compose.runtime.Composable
import de.oljg.glac.core.ui.components.DropDownSelector
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DIVIDER_LINE_ENDS
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.SELECTOR_PADDING

@Composable
fun DividerLineEndSelector(
    label: String,
    selectedDividerLineEnd: DividerLineEnd,
    onNewDividerLineEndSelected: (String) -> Unit
) {
    DropDownSelector(
        type = DividerLineEnd::class,
        startPadding = SELECTOR_PADDING / 3,
        label = label,
        selectedValue = selectedDividerLineEnd.name,
        onNewValueSelected = onNewDividerLineEndSelected,
        values = DIVIDER_LINE_ENDS
    )
}
