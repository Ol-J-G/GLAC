package de.oljg.glac.settings.clock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.DEFAULT_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MAX_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MIN_STROKE_WIDTH
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.settings.clock.ui.utils.prettyPrintPixel

@Composable
fun SevenSegmentSelector(
    selectedSevenSegmentWeight: String,
    onNewSevenSegmentWeightSelected: (String) -> Unit,
    selectedSevenSegmentStyle: String,
    onNewSevenSegmentStyleSelected: (String) -> Unit,
    isOutlineStyleSelected: Boolean,
    selectedOutlineSize: Float,
    onNewOutlineSizeSelected: (Float) -> Unit,
    onResetOutlineSize: () -> Unit,
    drawOffSegments: Boolean,
    onDrawOffSegmentsChanged: (Boolean) -> Unit
) {
    Column {
        SevenSegmentWeightSelector(
            label = "${stringResource(id = R.string.weight)}:",
            selectedSevenSegmentWeight = selectedSevenSegmentWeight,
            onNewSevenSegmentWeightSelected = onNewSevenSegmentWeightSelected
        )
        SevenSegmentStyleSelector(
            label = "${stringResource(id = R.string.style)}:    ",
            selectedSevenSegmentStyle = selectedSevenSegmentStyle,
            onNewSevenSegmentStyleSelected = onNewSevenSegmentStyleSelected
        )
        AnimatedVisibility(visible = isOutlineStyleSelected) {
            Column {
                Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
                SettingsSlider(
                    label = stringResource(R.string.outline_size),
                    value = selectedOutlineSize,
                    defaultValue = DEFAULT_STROKE_WIDTH,
                    sliderValuePrettyPrintFun = Float::prettyPrintPixel,
                    onValueChangeFinished = onNewOutlineSizeSelected,
                    onResetValue = onResetOutlineSize,
                    valueRange = MIN_STROKE_WIDTH..MAX_STROKE_WIDTH
                )
                Spacer(modifier = Modifier.fillMaxWidth().height(DEFAULT_VERTICAL_SPACE / 2))
            }
        }
        Divider(modifier = Modifier.padding(vertical = DEFAULT_VERTICAL_SPACE))
        SettingsSwitch(
            label = stringResource(R.string.off_segments),
            checked = drawOffSegments,
            onCheckedChange = onDrawOffSegmentsChanged
        )
    }
}

