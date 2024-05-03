package de.oljg.glac.settings.clock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.oljg.glac.R
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MAX_STROKE_WIDTH
import de.oljg.glac.clock.digital.ui.utils.SevenSegmentDefaults.MIN_STROKE_WIDTH
import de.oljg.glac.settings.clock.ui.components.common.SettingsSlider
import de.oljg.glac.settings.clock.ui.components.common.SettingsSwitch
import de.oljg.glac.settings.clock.ui.utils.prettyPrintOnePlace

@Composable
fun SevenSegmentSelector(
    selectedSevenSegmentWeight: String,
    onNewSevenSegmentWeightSelected: (String) -> Unit,
    selectedSevenSegmentStyle: String,
    onNewSevenSegmentStyleSelected: (String) -> Unit,
    isOutlineStyleSelected: Boolean,
    selectedOutlineSize: Float,
    onNewOutlineSizeSelected: (Float) -> Unit,
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
                Divider(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
                SettingsSlider(
                    label = stringResource(R.string.outline_size),
                    value = selectedOutlineSize,
                    sliderValuePrettyPrint = Float::prettyPrintOnePlace,
                    onValueChangeFinished = onNewOutlineSizeSelected,
                    valueRange = MIN_STROKE_WIDTH..MAX_STROKE_WIDTH
                )
            }
        }
        Divider(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
        SettingsSwitch(
            label = stringResource(R.string.off_segments),
            checked = drawOffSegments,
            onCheckedChange = onDrawOffSegmentsChanged
        )
    }
}

