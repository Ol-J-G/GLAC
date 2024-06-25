package de.oljg.glac.feature_clock.ui.settings.components.divider

import androidx.compose.runtime.Composable
import de.oljg.glac.feature_clock.ui.clock.utils.DividerLineEnd
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults

@Composable
fun DividerLineEndSelector(
    label: String,
    selectedDividerLineEnd: DividerLineEnd,
    onNewDividerLineEndSelected: (String) -> Unit
) {
    DropDownSelector(
        type = DividerLineEnd::class,
        startPadding = SettingsDefaults.DIALOG_DEFAULT_PADDING / 3,
        label = label,
        selectedValue = selectedDividerLineEnd.name,
        onNewValueSelected = onNewDividerLineEndSelected,
        values = SettingsDefaults.DIVIDER_LINE_ENDS
    )
}
