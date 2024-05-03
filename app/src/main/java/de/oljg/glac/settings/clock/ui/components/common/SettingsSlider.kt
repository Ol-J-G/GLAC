package de.oljg.glac.settings.clock.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun SettingsSlider(
    label: String,
    value: Float,
    sliderValuePrettyPrint: (Float) -> String = { floatValue -> floatValue.toString() },
    onValueChangeFinished: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
        var sliderValue by remember(value) { mutableFloatStateOf(value) }
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${label}:")
            Text(text = sliderValuePrettyPrint(sliderValue))
        }
        Slider(
            value = sliderValue,
            onValueChange = { newValue -> sliderValue = newValue },
            onValueChangeFinished = { onValueChangeFinished(sliderValue) },
            valueRange = valueRange,
        )
}

