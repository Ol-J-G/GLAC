package de.oljg.glac.settings.clock.ui.components.color

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import de.oljg.glac.R
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_BORDER_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_BUTTON_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_DEFAULT_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_FLASHING_COLOR_ANIM_DURATION
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_HEIGHT
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_SLIDER_HEIGHT

@Composable
fun ColorPickerDialog(
    onDismissRequest: () -> Unit,
    onColorChanged: (Color, String) -> Unit
) {
    val colorPickerController = rememberColorPickerController()
    val clipboardManager = LocalClipboardManager.current

    val defaultOnSurfaceColor = MaterialTheme.colorScheme.onSurface
    var selectedColor by remember {
        mutableStateOf(defaultOnSurfaceColor)
    }
    var selectedColorHexCode by remember {
        mutableStateOf("")
    }
    var copyToClipboardClicked by remember {
        mutableStateOf(false)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "iT")
    val flashingHexCodeColor by infiniteTransition.animateColor(
        initialValue = defaultOnSurfaceColor,
        targetValue = selectedColor,
        animationSpec = infiniteRepeatable(
            animation = tween(
                COLOR_PICKER_FLASHING_COLOR_ANIM_DURATION,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "iTaC"
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .border(
                    COLOR_PICKER_BORDER_WIDTH,
                    MaterialTheme.colorScheme.onTertiaryContainer
                )
        ) {
            Column {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(COLOR_PICKER_HEIGHT)
                        .padding(COLOR_PICKER_DEFAULT_PADDING * 2),
                    drawOnPosSelected = {
                        drawColorIndicator(
                            colorPickerController.selectedPoint.value,
                            colorPickerController.selectedColor.value,
                        )
                    },
                    controller = colorPickerController,
                    onColorChanged = { colorEnvelope ->
                        selectedColor = colorEnvelope.color
                        selectedColorHexCode = colorEnvelope.hexCode
                        copyToClipboardClicked = false
                    }
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(COLOR_PICKER_DEFAULT_PADDING)
                        .height(COLOR_PICKER_SLIDER_HEIGHT),
                    controller = colorPickerController,
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(COLOR_PICKER_DEFAULT_PADDING)
                        .height(COLOR_PICKER_SLIDER_HEIGHT),
                    controller = colorPickerController,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(COLOR_PICKER_DEFAULT_PADDING),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .clickable {
                                clipboardManager.setText(AnnotatedString(selectedColorHexCode))
                                copyToClipboardClicked = true
                            },
                        text = selectedColorHexCode,
                        color = if (copyToClipboardClicked)
                            selectedColor else flashingHexCodeColor,
                        fontFamily = FontFamily.Monospace,
                        fontStyle = FontStyle.Italic,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            COLOR_PICKER_BUTTON_SPACE, Alignment.End
                        )
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(text = stringResource(R.string.dismiss).uppercase())
                        }
                        TextButton(onClick = {
                            onColorChanged(selectedColor, selectedColorHexCode)
                            onDismissRequest.invoke()
                        }) {
                            Text(text = stringResource(R.string.confirm).uppercase())
                        }
                    }
                }
            }
        }
    }
}

