package de.oljg.glac.settings.clock.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_COLOR_SWATCH_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_HEIGHT
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_HEX_TEXTFIELD_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_TF_COLOR_SWATCH_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_TF_TOP_PADDING
import de.oljg.glac.settings.clock.ui.utils.argbHexCode
import de.oljg.glac.settings.clock.ui.utils.fromHexCode
import de.oljg.glac.settings.clock.ui.utils.isArgbHexCode


@Composable
fun ColorSelector(
    title: String,
    color: Color,
    onColorChanged: (Color) -> Unit
) {
    var showColorPicker by remember {
        mutableStateOf(false)
    }
    var textFieldInput by remember {
        mutableStateOf(color.argbHexCode())
    }
    var isValidInput by remember {
        mutableStateOf(true)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(COLOR_SELECTOR_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title) //TODO: add reset to default similar as in divider settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                COLOR_SELECTOR_TF_COLOR_SWATCH_SPACE, Alignment.End
            )
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .width(COLOR_SELECTOR_HEX_TEXTFIELD_WIDTH)
                    .padding(top = COLOR_SELECTOR_TF_TOP_PADDING),
                label = { Text(stringResource(R.string.hex_code)) },
                value = textFieldInput,
                onValueChange = { newValue ->
                    textFieldInput = newValue
                    isValidInput = textFieldInput.isArgbHexCode()
                    if (isValidInput) onColorChanged(Color.fromHexCode(textFieldInput))
                },
                supportingText = {
                    if (!isValidInput)
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.invalid_code) + "!",
                            color = MaterialTheme.colorScheme.error
                        )
                },
                singleLine = true,

            )
            Box(modifier = Modifier
                .clip(CircleShape)
                .size(COLOR_SELECTOR_COLOR_SWATCH_SIZE)
                .background(color)
                .clickable { showColorPicker = !showColorPicker }
            )
        }
    }
    AnimatedVisibility(visible = showColorPicker) {
        ColorPickerDialog(onDismissRequest = { showColorPicker = false }) { newColor, newHexCode ->
            onColorChanged(newColor)
            textFieldInput = newHexCode
        }
    }
}

