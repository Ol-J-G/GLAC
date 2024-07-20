package de.oljg.glac.feature_clock.ui.settings.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.EDGE_PADDING
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.RESET_BUTTON_SIZE
import de.oljg.glac.feature_clock.ui.settings.utils.SettingsDefaults.SETTINGS_SLIDER_HEIGHT


@Composable
fun SettingsSlider(
    label: String,
    value: Float,
    defaultValue: Float,
    sliderValuePrettyPrintFun: (Float) -> String = { floatValue -> floatValue.toString() },
    onValueChangeFinished: (Float) -> Unit,
    onResetValue: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float>? = null
) {
    var showResetButton by remember(key1 = value != defaultValue) {
        mutableStateOf(value != defaultValue)
    }
    val finalValueRange = valueRange ?: 0f..1f // percentage by default
    var sliderValue by remember(value) { mutableFloatStateOf(value) }
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(SETTINGS_SLIDER_HEIGHT)
        .padding(top = DEFAULT_VERTICAL_SPACE / 2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(EDGE_PADDING, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.padding(start = EDGE_PADDING), text = label)
            AnimatedVisibility(visible = showResetButton) {
                IconButton(onClick = {
                    showResetButton = false
                    onResetValue.invoke()
                }) {
                    Icon(
                        modifier = Modifier.size(RESET_BUTTON_SIZE),
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = stringResource(R.string.reset) + " $label",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        Text(modifier = Modifier.padding(end = EDGE_PADDING), text = sliderValuePrettyPrintFun(sliderValue))
    }
    Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    Slider(
        modifier = Modifier.padding(horizontal = EDGE_PADDING),
        value = sliderValue,
        onValueChange = { newValue -> sliderValue = newValue },
        onValueChangeFinished = {
            onValueChangeFinished(sliderValue)
            showResetButton = sliderValue != defaultValue
        },
        valueRange = finalValueRange
    )
}

