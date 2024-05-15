package de.oljg.glac.settings.clock.ui.components.divider

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import de.oljg.glac.clock.digital.ui.utils.DividerStyle
import de.oljg.glac.settings.clock.ui.components.common.DropDownSelector
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.prettyPrintEnumName

@Composable
fun DividerStyleSelector(
    label: String,
    selectedDividerStyle: DividerStyle,
    onNewDividerStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        label = label,
        selectedValue = selectedDividerStyle.name,
        onNewValueSelected = onNewDividerStyleSelected,
        values = if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            SettingsDefaults.DIVIDER_STYLES
        else SettingsDefaults.DIVIDER_STYLES_WITHOUT_CHAR_STYLE, // no char in portrait layout
        prettyPrintValue = String::prettyPrintEnumName
    )
}
