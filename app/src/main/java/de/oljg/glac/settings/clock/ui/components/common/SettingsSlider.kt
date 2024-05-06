package de.oljg.glac.settings.clock.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DEFAULT_VERTICAL_SPACE


@Composable
fun SettingsSlider(
    label: String,
    value: Float,
    sliderValuePrettyPrint: (Float) -> String = { floatValue -> floatValue.toString() },
    onValueChangeFinished: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>? = null
) {
    val finalValueRange = valueRange ?: 0f..1f // percentage by default
    var sliderValue by remember(value) { mutableFloatStateOf(value) }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = DEFAULT_VERTICAL_SPACE / 2),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${label}:")
        Text(text = sliderValuePrettyPrint(sliderValue))
    }
    Spacer(modifier = Modifier.fillMaxWidth().height(DEFAULT_VERTICAL_SPACE / 2))
    Slider(
        value = sliderValue,
        onValueChange = { newValue -> sliderValue = newValue },
        onValueChangeFinished = { onValueChangeFinished(sliderValue) },
        valueRange = finalValueRange
    )
}

