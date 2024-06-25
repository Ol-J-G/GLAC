package de.oljg.glac.feature_clock.ui.settings.components.divider

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.settings.components.common.DropDownSelector
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults

@Composable
fun DividerStyleSelector(
    label: String,
    selectedDividerStyle: DividerStyle,
    onNewDividerStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        type = DividerStyle::class,
        startPadding = SettingsDefaults.DIALOG_DEFAULT_PADDING / 3,
        label = label,
        selectedValue = selectedDividerStyle.name,
        onNewValueSelected = onNewDividerStyleSelected,
        values = if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            SettingsDefaults.DIVIDER_STYLES
        else SettingsDefaults.DIVIDER_STYLES_WITHOUT_CHAR_STYLE, // no char in portrait layout
    )
}
