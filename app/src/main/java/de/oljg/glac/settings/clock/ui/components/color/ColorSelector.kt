package de.oljg.glac.settings.clock.ui.components.color

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.TweenSpec
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.Dp
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_COLOR_SWATCH_SIZE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_HEIGHT
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_HEX_TEXTFIELD_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_TF_COLOR_SWATCH_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_SELECTOR_TF_TOP_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.EDGE_PADDING
import de.oljg.glac.settings.clock.ui.utils.argbHexCode
import de.oljg.glac.settings.clock.ui.utils.fromHexCode
import de.oljg.glac.settings.clock.ui.utils.isArgbHexCode
import kotlin.random.Random


@Composable
fun ColorSelector(
    title: String,
    startPadding: Dp = EDGE_PADDING / 2,
    endPadding: Dp = EDGE_PADDING / 2,
    color: Color,
    defaultColor: Color,
    onResetColor: () -> Unit,
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
    var showResetButton by remember(color) {
        mutableStateOf(color != defaultColor)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(COLOR_SELECTOR_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                COLOR_SELECTOR_TF_COLOR_SWATCH_SPACE, Alignment.Start
            )
        ) {
            Text(modifier = Modifier.padding(start = startPadding), text = title)
            Crossfade( // generate random color || reset color
                targetState = showResetButton,
                animationSpec = TweenSpec(),
                label = "crossfade"
            ) { targetState ->
                when (targetState) {
                    true -> IconButton(onClick = {
                        onResetColor.invoke()
                        showResetButton = false
                    }) {
                        Icon(
                            modifier = Modifier.size(SettingsDefaults.RESET_BUTTON_SIZE),
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = stringResource(R.string.reset) +
                                    " " + stringResource(R.string.color_for) + " $title",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    false -> IconButton(onClick = {
                        onColorChanged(Color(
                            red = Random.nextInt(until = 256), // until is exclusive => 0..255
                            green = Random.nextInt(until = 256),
                            blue = Random.nextInt(until = 256)
                        ))
                        showResetButton = true
                    }) {
                        Icon(
                            modifier = Modifier.size(SettingsDefaults.RESET_BUTTON_SIZE),
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = stringResource(R.string.generate_random) +
                                    " " + stringResource(R.string.color_for) + " $title",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                COLOR_SELECTOR_TF_COLOR_SWATCH_SPACE * 2, Alignment.End
            )
        ) {
            // Editable hex code of current selected color
            OutlinedTextField(
                modifier = Modifier
                    .width(COLOR_SELECTOR_HEX_TEXTFIELD_WIDTH)
                    .padding(top = COLOR_SELECTOR_TF_TOP_PADDING),
                label = { Text(stringResource(R.string.hex_code)) },
                value = textFieldInput,
                onValueChange = { newValue ->
                    textFieldInput = newValue
                    isValidInput = textFieldInput.isArgbHexCode()
                    if (isValidInput) {
                        onColorChanged(Color.fromHexCode(textFieldInput))
                        showResetButton = true
                    }
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

            // Current selected color
            Box(modifier = Modifier
                .padding(end = endPadding)
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
            showResetButton = true
        }
    }
}

