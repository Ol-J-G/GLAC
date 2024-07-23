package de.oljg.glac.feature_clock.ui.settings.components.divider

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import de.oljg.glac.core.ui.components.DropDownSelector
import de.oljg.glac.feature_clock.ui.clock.utils.DividerStyle
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DIVIDER_STYLES
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DIVIDER_STYLES_WITHOUT_CHAR_STYLE
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.SELECTOR_PADDING

@Composable
fun DividerStyleSelector(
    label: String,
    selectedDividerStyle: DividerStyle,
    onNewDividerStyleSelected: (String) -> Unit
) {
    DropDownSelector(
        type = DividerStyle::class,
        startPadding = SELECTOR_PADDING / 3,
        label = label,
        selectedValue = selectedDividerStyle.name,
        onNewValueSelected = onNewDividerStyleSelected,
        values = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
            DIVIDER_STYLES else DIVIDER_STYLES_WITHOUT_CHAR_STYLE // no char in portrait layout
    )
}
