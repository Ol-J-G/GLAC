package de.oljg.glac.settings.clock.ui.components.color

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.SettingsDialog
import de.oljg.glac.core.util.ScreenDetails
import de.oljg.glac.core.util.defaultColor
import de.oljg.glac.core.util.screenDetails
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_DIALOG_WIDTH
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_BUTTON_SPACE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_FLASHING_COLOR_ANIM_DURATION
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_HEIGHT_EXTRA_SMALL
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_HEIGHT_LARGE
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_HEIGHT_MEDIUM
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_HEIGHT_SMALL
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.COLOR_PICKER_SLIDER_HEIGHT
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.DIALOG_DEFAULT_PADDING
import de.oljg.glac.settings.clock.ui.utils.SettingsDefaults.colorSaver

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onColorChanged: (Color, String) -> Unit
) {
    val colorPickerController = rememberColorPickerController()
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current

    val defaultColor = defaultColor()
    var selectedColor by rememberSaveable(stateSaver = colorSaver) {
        mutableStateOf(defaultColor)
    }
    var selectedColorHexCode by rememberSaveable {
        mutableStateOf("")
    }
    var copyToClipboardClicked by remember {
        mutableStateOf(false)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "iT")
    val flashingHexCodeColor by infiniteTransition.animateColor(
        initialValue = defaultColor,
        targetValue = selectedColor,
        animationSpec = infiniteRepeatable(
            animation = tween(
                COLOR_PICKER_FLASHING_COLOR_ANIM_DURATION,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = "iTaC"
    )

    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType

    SettingsDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        maxWidthFraction = COLOR_DIALOG_WIDTH
    ) {
        Column {
            Column(
                modifier = Modifier
                    .weight(3f, fill = false)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top
            ) {
                when {
                    (screenWidthType is ScreenDetails.DisplayType.Medium
                            && screenHeightType is ScreenDetails.DisplayType.Compact)
                            || screenWidthType is ScreenDetails.DisplayType.Expanded ->
                        TwoColumnsColorPicker(
                            colorPickerController = colorPickerController,
                            initialColor = initialColor,
                        ) { colorEnvelope ->
                            selectedColor = colorEnvelope.color
                            selectedColorHexCode = colorEnvelope.hexCode
                            copyToClipboardClicked = false
                        }
                    else ->
                        OneColumnColorPicker(
                            colorPickerController = colorPickerController,
                            initialColor = initialColor,
                        ) { colorEnvelope ->
                            selectedColor = colorEnvelope.color
                            selectedColorHexCode = colorEnvelope.hexCode
                            copyToClipboardClicked = false
                        }
                }
            }

            // hexcode/dismiss-/confirm-button row
            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DIALOG_DEFAULT_PADDING)
                        .padding(horizontal = DIALOG_DEFAULT_PADDING / 2),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = DIALOG_DEFAULT_PADDING / 1.5f)
                            .clickable {
                                clipboardManager.setText(
                                    AnnotatedString(selectedColorHexCode)
                                )
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


@Composable
private fun TwoColumnsColorPicker(
    colorPickerController: ColorPickerController,
    initialColor: Color,
    onColorChanged: (ColorEnvelope) -> Unit
) {
    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            HSVColorSelector(
                initialColor = initialColor,
                colorPickerController = colorPickerController,
                height = when  {
                    screenWidthType is ScreenDetails.DisplayType.Medium &&
                             screenHeightType is ScreenDetails.DisplayType.Compact ->
                                COLOR_PICKER_HEIGHT_EXTRA_SMALL
                    screenWidthType is ScreenDetails.DisplayType.Expanded &&
                             screenHeightType is ScreenDetails.DisplayType.Compact ->
                        COLOR_PICKER_HEIGHT_SMALL

                    else -> COLOR_PICKER_HEIGHT_LARGE
                },
                onColorChanged = onColorChanged
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlphaSelector(colorPickerController = colorPickerController)
            BrightnessSelector(colorPickerController = colorPickerController)
        }
    }
}


@Composable
private fun OneColumnColorPicker(
    colorPickerController: ColorPickerController,
    initialColor: Color,
    onColorChanged: (ColorEnvelope) -> Unit
) {
    val screenDetails = screenDetails()
    val screenWidthType = screenDetails.screenWidthType
    val screenHeightType = screenDetails.screenHeightType
    HSVColorSelector(
        colorPickerController = colorPickerController,
        initialColor = initialColor,
        height = when  {
            screenWidthType is ScreenDetails.DisplayType.Compact
                    && screenHeightType is ScreenDetails.DisplayType.Medium ->
                        COLOR_PICKER_HEIGHT_EXTRA_SMALL
            screenWidthType is ScreenDetails.DisplayType.Compact -> COLOR_PICKER_HEIGHT_MEDIUM

            else -> COLOR_PICKER_HEIGHT_LARGE
        },
        onColorChanged = onColorChanged
    )
    AlphaSelector(colorPickerController = colorPickerController)
    BrightnessSelector(colorPickerController = colorPickerController)
}


@Composable
private fun HSVColorSelector(
    colorPickerController: ColorPickerController,
    height: Dp,
    initialColor: Color,
    onColorChanged: (ColorEnvelope) -> Unit
) {
    HsvColorPicker(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(DIALOG_DEFAULT_PADDING * 2)
            .padding(top = DIALOG_DEFAULT_PADDING),
        drawOnPosSelected = {
            drawColorIndicator(
                colorPickerController.selectedPoint.value,
                colorPickerController.selectedColor.value,
            )
        },
        controller = colorPickerController,
        onColorChanged = onColorChanged,
        initialColor = initialColor
    )
}


@Composable
private fun AlphaSelector(colorPickerController: ColorPickerController) {
    AlphaSlider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DIALOG_DEFAULT_PADDING * 2)
            .height(COLOR_PICKER_SLIDER_HEIGHT),
        controller = colorPickerController,
    )
}


@Composable
private fun BrightnessSelector(colorPickerController: ColorPickerController) {
    BrightnessSlider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DIALOG_DEFAULT_PADDING * 2)
            .height(COLOR_PICKER_SLIDER_HEIGHT),
        controller = colorPickerController,
    )
}
