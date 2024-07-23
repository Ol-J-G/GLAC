package de.oljg.glac.feature_clock.ui.settings.components.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import de.oljg.glac.R
import de.oljg.glac.core.ui.components.DropDownSelector
import de.oljg.glac.core.ui.components.ExpandableSection
import de.oljg.glac.core.ui.components.GlacAlertDialog
import de.oljg.glac.core.util.CommonUtils.SPACE
import de.oljg.glac.feature_clock.domain.model.ClockSettings
import de.oljg.glac.feature_clock.domain.model.ClockTheme
import de.oljg.glac.feature_clock.domain.model.utils.ClockSettingsDefaults.DEFAULT_THEME_NAME
import de.oljg.glac.feature_clock.ui.ClockSettingsEvent
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_ICON_BUTTON_SIZE
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.DEFAULT_VERTICAL_SPACE
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.MAX_THEME_NAME_LENGTH
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.MIN_THEME_NAME_LENGTH
import de.oljg.glac.feature_clock.ui.settings.utils.ClockSettingsDefaults.RESET_BUTTON_SIZE

@Composable
fun ClockThemeSettings(
    clockSettings: ClockSettings,
    onEvent: (ClockSettingsEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var textFieldValue by remember(clockSettings.clockThemeName) {
        mutableStateOf(clockSettings.clockThemeName)
    }
    var isValidInput by remember {
        mutableStateOf(true)
    }

    var showResetConfirmationDialog by rememberSaveable(key = textFieldValue) {
        mutableStateOf(false)
    }
    var showRemoveConfirmationDialog by rememberSaveable(key = textFieldValue) {
        mutableStateOf(false)
    }

    ExpandableSection(
        sectionTitle = stringResource(R.string.theme),
        expanded = clockSettings.clockSettingsSectionThemeIsExpanded,
        onExpandedChange = { expanded ->
            onEvent(ClockSettingsEvent.UpdateClockSettingsSectionThemeIsExpanded(expanded))
        }
    ) {
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
        DropDownSelector(
            label = stringResource(R.string.theme_name),
            selectedValue = textFieldValue,
            onNewValueSelected = { selectedThemeName ->
                onEvent(ClockSettingsEvent.UpdateClockThemeName(selectedThemeName))
                textFieldValue = selectedThemeName
                isValidInput = true
            },
            values = clockSettings.themes.keys.toList(),
            readOnly = false,
            onTextFieldValueChanged = { newValue ->
                textFieldValue = newValue
                isValidInput = textFieldValue.isNotBlank()
                        && textFieldValue.length <= MAX_THEME_NAME_LENGTH
            },
            supportingText = {
                if (!isValidInput)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = when {
                            textFieldValue.isBlank() -> stringResource(R.string.please_enter_a_name)
                            textFieldValue.length > MAX_THEME_NAME_LENGTH ->
                                "${stringResource(R.string.invalid_lenght)}! " +
                                        "${stringResource(R.string.allowed)}: " +
                                        "$MIN_THEME_NAME_LENGTH - $MAX_THEME_NAME_LENGTH"

                            else -> ""
                        },
                        color = MaterialTheme.colorScheme.error
                    )
            },
            resetValueComponent = {
                IconButton(
                    onClick = { showResetConfirmationDialog = true },
                    enabled = clockSettings.themes.containsKey(textFieldValue)
                            && clockSettings.themes[textFieldValue] != ClockTheme(),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(RESET_BUTTON_SIZE),
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = stringResource(R.string.reset_theme)
                    )
                }
            },
            removeValueComponent = {
                IconButton(
                    onClick = { showRemoveConfirmationDialog = true },
                    enabled = textFieldValue != DEFAULT_THEME_NAME
                            && clockSettings.themes.containsKey(textFieldValue),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
                        imageVector = Icons.Filled.RemoveCircleOutline,
                        contentDescription = stringResource(R.string.remove_theme)
                    )
                }
            },
            addValueComponent = {
                IconButton(
                    onClick = {
                        // Create a new theme based on current theme
                        onEvent(ClockSettingsEvent.UpdateClockThemeName(textFieldValue))
                        onEvent(
                            ClockSettingsEvent.UpdateThemes(
                                textFieldValue,
                                clockSettings.themes.getOrDefault(
                                    key = clockSettings.clockThemeName,
                                    defaultValue = ClockTheme()
                                )
                            )
                        )
                        keyboardController?.hide()
                    },
                    enabled = textFieldValue != DEFAULT_THEME_NAME
                            && !clockSettings.themes.containsKey(textFieldValue)
                            && isValidInput,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(DEFAULT_ICON_BUTTON_SIZE),
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = stringResource(R.string.add_theme)
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(DEFAULT_VERTICAL_SPACE / 2))
    }

    AnimatedVisibility(
        visible = showResetConfirmationDialog,
        enter = fadeIn(TweenSpec(durationMillis = 100)),
        exit = fadeOut(TweenSpec(durationMillis = 100))
    ) {
        GlacAlertDialog(
            title = stringResource(R.string.reset_theme),
            message = stringResource(R.string.do_you_really_want_to_reset_theme)
                    + SPACE + "'$textFieldValue'?",
            onDismissRequest = { showResetConfirmationDialog = false },
            onConfirm = {
                // Reset current theme to default theme
                onEvent(ClockSettingsEvent.UpdateThemes(textFieldValue, ClockTheme()))
            }
        )
    }

    AnimatedVisibility(
        visible = showRemoveConfirmationDialog,
        enter = fadeIn(TweenSpec(durationMillis = 100)),
        exit = fadeOut(TweenSpec(durationMillis = 100))
    ) {
        GlacAlertDialog(
            title = stringResource(R.string.remove_theme),
            message = stringResource(R.string.do_you_really_want_to_remove_theme)
                    + SPACE + "'$textFieldValue'?",
            onDismissRequest = { showRemoveConfirmationDialog = false },
            onConfirm = {
                onEvent(ClockSettingsEvent.RemoveTheme(textFieldValue))
                textFieldValue = DEFAULT_THEME_NAME
                onEvent(ClockSettingsEvent.UpdateClockThemeName(DEFAULT_THEME_NAME))
            }
        )
    }
}
